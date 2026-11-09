-- Temperature Converter database
-- DB localization approach: TRANSLATION TABLE
--   unit              language-neutral data (key, category, symbol, conversion factor)
--   unit_translation  one row per unit and language (name, description)
--   conversion_record saved conversions, linked to unit twice
-- Everything uses utf8mb4 so Japanese and Arabic text is stored correctly.

CREATE DATABASE IF NOT EXISTS temperature_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE temperature_db;
SET NAMES utf8mb4;

-- Unit / lookup data (no translatable text here)
CREATE TABLE IF NOT EXISTS unit (
    id INT PRIMARY KEY,
    unit_key VARCHAR(30) NOT NULL UNIQUE,
    category VARCHAR(20) NOT NULL,          -- temperature, speed, distance, time
    symbol VARCHAR(10) NOT NULL,
    to_base DOUBLE NULL                     -- factor to the category base unit; NULL for temperature
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Localization structure
CREATE TABLE IF NOT EXISTS unit_translation (
    unit_id INT NOT NULL,
    lang CHAR(2) NOT NULL,                  -- en, fi, sv, ja, ar
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255) NOT NULL,
    PRIMARY KEY (unit_id, lang),
    FOREIGN KEY (unit_id) REFERENCES unit(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Measurement table: each saved conversion
CREATE TABLE IF NOT EXISTS conversion_record (
    id INT AUTO_INCREMENT PRIMARY KEY,
    input_value DOUBLE NOT NULL,
    from_unit_id INT NOT NULL,
    result_value DOUBLE NOT NULL,
    to_unit_id INT NOT NULL,
    lang CHAR(2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (from_unit_id) REFERENCES unit(id),
    FOREIGN KEY (to_unit_id) REFERENCES unit(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO unit (id, unit_key, category, symbol, to_base) VALUES
    (1,  'celsius',            'temperature', '°C',   NULL),
    (2,  'fahrenheit',         'temperature', '°F',   NULL),
    (3,  'kelvin',             'temperature', 'K',    NULL),
    (4,  'metre_per_second',   'speed',       'm/s',  1),
    (5,  'kilometre_per_hour', 'speed',       'km/h', 0.2777777777777778),
    (6,  'mile_per_hour',      'speed',       'mph',  0.44704),
    (7,  'metre',              'distance',    'm',    1),
    (8,  'kilometre',          'distance',    'km',   1000),
    (9,  'mile',               'distance',    'mi',   1609.344),
    (10, 'second',             'time',        's',    1),
    (11, 'minute',             'time',        'min',  60),
    (12, 'hour',               'time',        'h',    3600)
ON DUPLICATE KEY UPDATE unit_key = VALUES(unit_key), category = VALUES(category),
    symbol = VALUES(symbol), to_base = VALUES(to_base);

INSERT INTO unit_translation (unit_id, lang, name, description) VALUES
    -- Celsius
    (1, 'en', 'Celsius', 'Water freezes at 0 °C and boils at 100 °C.'),
    (1, 'fi', 'Celsius', 'Vesi jäätyy 0 °C:ssa ja kiehuu 100 °C:ssa.'),
    (1, 'sv', 'Celsius', 'Vatten fryser vid 0 °C och kokar vid 100 °C.'),
    (1, 'ja', '摂氏', '水は0 °Cで凍り、100 °Cで沸騰します。'),
    (1, 'ar', 'درجة مئوية', 'يتجمد الماء عند 0 °C ويغلي عند 100 °C.'),
    -- Fahrenheit
    (2, 'en', 'Fahrenheit', 'Water freezes at 32 °F and boils at 212 °F.'),
    (2, 'fi', 'Fahrenheit', 'Vesi jäätyy 32 °F:ssa ja kiehuu 212 °F:ssa.'),
    (2, 'sv', 'Fahrenheit', 'Vatten fryser vid 32 °F och kokar vid 212 °F.'),
    (2, 'ja', '華氏', '水は32 °Fで凍り、212 °Fで沸騰します。'),
    (2, 'ar', 'فهرنهايت', 'يتجمد الماء عند 32 °F ويغلي عند 212 °F.'),
    -- Kelvin
    (3, 'en', 'Kelvin', 'Absolute scale: 0 K is absolute zero (−273.15 °C).'),
    (3, 'fi', 'Kelvin', 'Absoluuttinen asteikko: 0 K on absoluuttinen nollapiste (−273,15 °C).'),
    (3, 'sv', 'Kelvin', 'Absolut skala: 0 K är absoluta nollpunkten (−273,15 °C).'),
    (3, 'ja', 'ケルビン', '絶対温度の単位。0 Kは絶対零度(−273.15 °C)です。'),
    (3, 'ar', 'كلفن', 'مقياس مطلق: 0 K هو الصفر المطلق (−273.15 °C).'),
    -- metre per second
    (4, 'en', 'metre per second', 'SI unit of speed.'),
    (4, 'fi', 'metriä sekunnissa', 'Nopeuden SI-yksikkö.'),
    (4, 'sv', 'meter per sekund', 'SI-enheten för hastighet.'),
    (4, 'ja', 'メートル毎秒', '速度のSI単位。'),
    (4, 'ar', 'متر في الثانية', 'وحدة السرعة في النظام الدولي.'),
    -- kilometre per hour
    (5, 'en', 'kilometre per hour', 'Common unit for road speeds.'),
    (5, 'fi', 'kilometriä tunnissa', 'Yleinen tieliikenteen nopeuden yksikkö.'),
    (5, 'sv', 'kilometer per timme', 'Vanlig enhet för hastighet på vägar.'),
    (5, 'ja', 'キロメートル毎時', '道路の速度でよく使われる単位。'),
    (5, 'ar', 'كيلومتر في الساعة', 'وحدة شائعة لسرعة المركبات على الطرق.'),
    -- mile per hour
    (6, 'en', 'mile per hour', 'Road speed unit used in the US and UK.'),
    (6, 'fi', 'mailia tunnissa', 'Yhdysvalloissa ja Britanniassa käytetty nopeuden yksikkö.'),
    (6, 'sv', 'engelska mil per timme', 'Hastighetsenhet som används i USA och Storbritannien.'),
    (6, 'ja', 'マイル毎時', '米国と英国で使われる速度の単位。'),
    (6, 'ar', 'ميل في الساعة', 'وحدة سرعة تُستخدم في الولايات المتحدة والمملكة المتحدة.'),
    -- metre
    (7, 'en', 'metre', 'SI base unit of length.'),
    (7, 'fi', 'metri', 'Pituuden SI-perusyksikkö.'),
    (7, 'sv', 'meter', 'SI-grundenheten för längd.'),
    (7, 'ja', 'メートル', '長さのSI基本単位。'),
    (7, 'ar', 'متر', 'الوحدة الأساسية للطول في النظام الدولي.'),
    -- kilometre
    (8, 'en', 'kilometre', '1000 metres.'),
    (8, 'fi', 'kilometri', '1000 metriä.'),
    (8, 'sv', 'kilometer', '1000 meter.'),
    (8, 'ja', 'キロメートル', '1000メートル。'),
    (8, 'ar', 'كيلومتر', '1000 متر.'),
    -- mile
    (9, 'en', 'mile', '1609.344 metres.'),
    (9, 'fi', 'maili', '1609,344 metriä.'),
    (9, 'sv', 'engelsk mil', '1609,344 meter.'),
    (9, 'ja', 'マイル', '1609.344メートル。'),
    (9, 'ar', 'ميل', '1609.344 متر.'),
    -- second
    (10, 'en', 'second', 'SI base unit of time.'),
    (10, 'fi', 'sekunti', 'Ajan SI-perusyksikkö.'),
    (10, 'sv', 'sekund', 'SI-grundenheten för tid.'),
    (10, 'ja', '秒', '時間のSI基本単位。'),
    (10, 'ar', 'ثانية', 'الوحدة الأساسية للزمن في النظام الدولي.'),
    -- minute
    (11, 'en', 'minute', '60 seconds.'),
    (11, 'fi', 'minuutti', '60 sekuntia.'),
    (11, 'sv', 'minut', '60 sekunder.'),
    (11, 'ja', '分', '60秒。'),
    (11, 'ar', 'دقيقة', '60 ثانية.'),
    -- hour
    (12, 'en', 'hour', '60 minutes.'),
    (12, 'fi', 'tunti', '60 minuuttia.'),
    (12, 'sv', 'timme', '60 minuter.'),
    (12, 'ja', '時間', '60分。'),
    (12, 'ar', 'ساعة', '60 دقيقة.')
ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description);
