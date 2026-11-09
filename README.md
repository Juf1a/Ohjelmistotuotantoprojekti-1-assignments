# Temperature Converter

## Assignment Description

A JavaFX desktop application that converts temperatures (Celsius, Fahrenheit,
Kelvin) and also speed, distance and time units. The user picks a quantity,
a source unit and a target unit, enters a value, and sees the result.
Temperatures below -40 °C or above 50 °C are flagged as extreme. Every
conversion is stored in MariaDB together with the language that was active,
and the interface shows the five most recent conversions.

The interface is available in English, Finnish, Swedish, Japanese and Arabic.
Two layers are localized:

- **Static UI labels** (title, buttons, field labels, messages) come from
  `ResourceBundle` `.properties` files.
- **Unit names and descriptions** (for example "kilometre per hour",
  "摂氏", "درجة مئوية") come from the database in the selected language.

The language selector in the window changes both layers at once: it reloads
the FXML scene with the new bundle, and the controller queries the database
in the new language. Arabic switches the layout to right-to-left.

## Database Localization Approach: Translation Table

**Chosen approach:** translation table. Unit data that does not depend on the
language lives in a base table `unit`, and every translatable text lives in
`unit_translation`, linked to it by a foreign key.

```
unit                                 unit_translation
---------------------------------    ------------------------------------
id        INT PK                 1──*  unit_id     INT FK → unit.id  ┐ PK
unit_key  VARCHAR UNIQUE              lang        CHAR(2)           ┘
category  temperature|speed|...       name        VARCHAR(100)
symbol    °C, km/h, ...               description VARCHAR(255)
to_base   DOUBLE (factor, NULL
          for temperature)       conversion_record
                                 ------------------------------------
                                 id, input_value, result_value,
                                 from_unit_id FK → unit.id,
                                 to_unit_id   FK → unit.id,
                                 lang, created_at
```

`UnitDAO` reads the names with one query that joins the translation twice,
once for the selected language and once for English, and uses `COALESCE` to
fall back to English when a translation is missing:

```sql
SELECT u.id, u.unit_key, u.category, u.symbol, u.to_base,
       COALESCE(t.name, d.name, u.unit_key) AS name,
       COALESCE(t.description, d.description, '') AS description
FROM unit u
LEFT JOIN unit_translation t ON t.unit_id = u.id AND t.lang = ?
LEFT JOIN unit_translation d ON d.unit_id = u.id AND d.lang = 'en'
```

**Why this approach:**

- **No duplicated non-text data.** The symbol, category and conversion factor
  of each unit are stored once. With the *row* approach every language row
  would repeat them, and a wrong factor could be fixed in one language but
  not the others.
- **Adding a language means adding rows only.** No schema change (unlike the
  *field* approach with `name_en`, `name_fi`, …) and no new table or runtime
  table-name switching (unlike the *table* approach).
- **The English fallback is built into the query.** A unit that is not yet
  translated still shows a readable name instead of an empty dropdown entry.
  The unit tests check this with a unit that only has an English
  translation.
- **The saved data stays language-neutral.** `conversion_record` points to
  `unit.id`, so the history list is shown with unit names in whatever
  language is selected now. The `lang` column records which language the
  conversion was made in.
- The cost is one extra JOIN per query, which is negligible for a dozen units.

All tables use `utf8mb4` with `utf8mb4_unicode_ci` collation. MariaDB
Connector/J 3 always uses `utf8mb4` for the connection, so Japanese and
Arabic are stored and read back without loss.

## Technologies & Tools Used

- Java 17
- JavaFX Controls and FXML 21.0.1 for the desktop user interface
- MariaDB with Connector/J 3.3.3 and JDBC
- Java `ResourceBundle` and UTF-8 `.properties` files for UI labels
- Maven, JUnit Jupiter 5.9.2 and JaCoCo 0.8.11
- Docker, Jenkins, Kubernetes (minikube)
- XQuartz / Xming for displaying the GUI from the Docker container

## Design & Implementation

- `Main` loads `MainView.fxml` with the bundle of the selected `Language`,
  sets right-to-left orientation for Arabic, and reloads the scene when the
  language changes.
- `Language` maps each option to its `Locale` (bundle) and its two-letter
  code (database `lang` value).
- `MainController` fills the quantity and unit dropdowns from `UnitDAO`,
  shows the unit descriptions, converts, saves the conversion, and shows the
  last 5 conversions.
- `TemperatureConverter` converts temperatures through Celsius and linear
  units (speed, distance, time) through the `to_base` factor.
- `UnitDAO` returns localized units with English fallback.
- `ConversionDAO` saves conversions and returns the most recent ones.
- `database/init.sql` creates the database and tables and fills 12 units
  with names and descriptions in all five languages.

### UI label bundles

| File | Language |
|---|---|
| `MessagesBundle_en_US.properties` | English (default) |
| `MessagesBundle_fi_FI.properties` | Finnish |
| `MessagesBundle_sv_SE.properties` | Swedish |
| `MessagesBundle_ja_JP.properties` | Japanese |
| `MessagesBundle_ar_SA.properties` | Arabic |

The quantity names (Temperature, Speed, …) are fixed UI labels, so they are
kept in the bundles (`category.*` keys). Unit names and descriptions are not
in the bundles.

## Testing & Quality Assurance

```sh
mvn clean test        # JaCoCo report: target/site/jacoco/index.html
```

On 2026-11-09 with MariaDB running: **55 tests, 0 failures, 0 errors, 0
skipped**. Tests cover:

- all temperature conversion pairs, speed/distance/time conversions, invalid
  units, and the extreme-temperature boundaries
- `UnitDAO`: English, Japanese and Arabic names, all units translated into
  all five languages, English fallback for a missing translation and for an
  unknown language
- `ConversionDAO`: saving with the language, newest-first ordering and
  limit, and unit names shown in another language
- the models, `Language`, and the JavaFX view in every language, including
  runtime switching from English to Arabic (title and RTL)

JaCoCo line coverage on 2026-11-09: `TemperatureConverter`, `UnitDAO`,
`ConversionDAO`, `Unit`, `Conversion` and `Language` all **100%**. Database
tests are skipped automatically when MariaDB is not reachable.

## How to Run

### Database

```sh
mysql -u root -p < database/init.sql
```

The script can be run again safely: it updates the seeded units and
translations in place. The application defaults to
`jdbc:mariadb://localhost:3306/temperature_db`, user `root`, empty password.
Override with `DB_URL`, `DB_USER` and `DB_PASSWORD`.

### Application

```sh
mvn javafx:run
```

## Docker & CI/CD

The `Dockerfile` builds a multi-architecture image that includes Japanese
fonts. The `Jenkinsfile` builds, runs the tests, publishes the JUnit and
JaCoCo results, and pushes `mikeluong/temperature-converter` to Docker Hub.

Run the container with an X server (XQuartz on macOS, Xming on Windows):

```sh
docker run --rm -e DISPLAY=host.docker.internal:0 mikeluong/temperature-converter
```

`temperature-converter.yaml` deploys the image to minikube. The database
runs on the host and is reached through `host.minikube.internal`.
