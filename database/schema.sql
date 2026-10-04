CREATE DATABASE IF NOT EXISTS temperature_db;
USE temperature_db;

CREATE TABLE IF NOT EXISTS temperature_unit (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    symbol VARCHAR(5) NOT NULL
);

CREATE TABLE IF NOT EXISTS temp_record (
    id INT AUTO_INCREMENT PRIMARY KEY,
    input_value DOUBLE NOT NULL,
    from_unit_id INT NOT NULL,
    result_value DOUBLE NOT NULL,
    to_unit_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (from_unit_id) REFERENCES temperature_unit(id),
    FOREIGN KEY (to_unit_id) REFERENCES temperature_unit(id)
);

INSERT IGNORE INTO temperature_unit (name, symbol) VALUES
    ('Celsius', '°C'),
    ('Fahrenheit', '°F');
