# Temperature Converter

## Assignment Description

This assignment implements a desktop application for converting temperatures
between Fahrenheit and Celsius. Users can enter a numeric temperature, choose
the conversion direction, and view the result. The application flags results
whose Celsius equivalent is below -40 or above 50 degrees and stores successful
conversions in a MariaDB database. The interface displays the ten most recent
saved conversions.

The deliverable is a JavaFX application, its MariaDB schema, and automated
JUnit tests. A Kelvin-to-Celsius conversion is also implemented in the
conversion logic, though it is not currently offered as an option in the GUI.

## Technologies & Tools Used

- Java 17
- JavaFX Controls 17.0.9 for the desktop user interface
- MariaDB for persistent conversion history
- MariaDB Connector/J 3.3.3 and JDBC for database access
- Maven for dependency management, building, and running
- JUnit Jupiter 5.9.2 for automated tests
- JaCoCo 0.8.11 for test coverage reports
- Docker for containerizing the application
- Jenkins for the CI/CD pipeline (build, test, coverage, Docker image push)
- XQuartz for displaying the JavaFX GUI from the Docker container on macOS

## Design Approach & Implementation Method

The solution separates the user interface, conversion logic, and database
access:

- `Main` creates the JavaFX interface, validates numeric input, selects the
  conversion direction, shows the result, and refreshes the recent-history
  list.
- `TemperatureConverter` implements Fahrenheit/Celsius conversions, the
  extreme-temperature check, and Kelvin-to-Celsius conversion.
- `TemperatureUnit` and `TempRecord` represent units and saved conversions.
- `TemperatureUnitDAO` and `TempRecordDAO` use JDBC to look up units, save
  conversion records, and retrieve recent history.
- `DBConnection` reads database connection settings from environment
  variables, with local defaults.
- `database/schema.sql` creates the database and required tables and seeds the
  Celsius and Fahrenheit units.

The history is stored in MariaDB so that it can persist between application
runs. Conversion queries use prepared statements. Invalid numeric input is
reported in the interface and is not saved.

## Testing & Quality Assurance

### Automated tests

Run the test suite with:

```sh
mvn test
```

On 2026-10-09, `mvn test` completed successfully: **26 tests, 0 failures, 0
errors, and 0 skipped**. Tests cover conversion values and
extreme-temperature boundaries, model behavior, database configuration,
temperature-unit queries, saving and retrieving conversion records, and
JavaFX application startup. The database-backed tests ran against MariaDB.

Maven also generates a JaCoCo report at `target/site/jacoco/index.html`.

### Runtime and manual checks

On 2026-10-09, the MariaDB schema was applied using `database/schema.sql`.
Verification confirmed both application tables exist and the Celsius and
Fahrenheit units are seeded. The database-backed automated tests successfully
saved and retrieved conversion records, checked newest-first ordering and
history limits, and looked up the temperature units.

`mvn javafx:run` launched the JavaFX application with MariaDB running. The
GUI was manually tested:

1. Converted `32` °F to Celsius: result `0.00`.
2. Converted `100` °C to Fahrenheit: result `212.00`.
3. Entered non-numeric text: the application requested a valid number and did
   not save a conversion.
4. Converted values below -40 °C and above 50 °C: the result included the
   extreme-temperature warning.
5. Completed a conversion: it appeared in the recent-conversions list. After
   restarting the application, the saved history remained.

## How to Run

### Prerequisites

- JDK 17
- Apache Maven
- A running MariaDB server
- A graphical desktop session to display the JavaFX window

### Configure the database

From the project root, create the database and tables by running the supplied
schema script with a MariaDB account that can create databases:

```sh
mysql -u root -p < database/schema.sql
```

The application defaults to `jdbc:mariadb://localhost:3306/temperature_db`,
user `root`, and an empty password. If your local database uses different
settings, set these environment variables before starting the application:

```sh
export DB_URL="jdbc:mariadb://localhost:3306/temperature_db"
export DB_USER="your_database_user"
export DB_PASSWORD="your_database_password"
```

### Run the application

From the project root:

```sh
mvn javafx:run
```

Maven downloads the project dependencies as needed and launches the JavaFX
application.


## Docker & CI/CD

The `Dockerfile` packages the application as a Docker image. The `Jenkinsfile`
defines a Jenkins pipeline that builds, tests, generates the coverage report,
and pushes the Docker image to Docker Hub.
