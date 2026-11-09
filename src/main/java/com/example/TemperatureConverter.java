package com.example;

public class TemperatureConverter {

    public static final String CELSIUS = "celsius";
    public static final String FAHRENHEIT = "fahrenheit";
    public static final String KELVIN = "kelvin";
    public static final String TEMPERATURE = "temperature";

    public double fahrenheitToCelsius(double fahrenheit) {
        return (fahrenheit - 32) * 5 / 9;
    }

    public double celsiusToFahrenheit(double celsius) {
        return (celsius * 9 / 5) + 32;
    }

    public double kelvinToCelsius(double kelvin) {
        return kelvin - 273.15;
    }

    public double celsiusToKelvin(double celsius) {
        return celsius + 273.15;
    }

    public boolean isExtremeTemperature(double celsius) {
        return celsius < -40 || celsius > 50;
    }

    public double toCelsius(double value, String unitKey) {
        return switch (unitKey) {
            case CELSIUS -> value;
            case FAHRENHEIT -> fahrenheitToCelsius(value);
            case KELVIN -> kelvinToCelsius(value);
            default -> throw new IllegalArgumentException("Unknown temperature unit: " + unitKey);
        };
    }

    public double fromCelsius(double celsius, String unitKey) {
        return switch (unitKey) {
            case CELSIUS -> celsius;
            case FAHRENHEIT -> celsiusToFahrenheit(celsius);
            case KELVIN -> celsiusToKelvin(celsius);
            default -> throw new IllegalArgumentException("Unknown temperature unit: " + unitKey);
        };
    }

    /** Converts between two temperature units given by their unit_key. */
    public double convertTemperature(double value, String fromKey, String toKey) {
        return fromCelsius(toCelsius(value, fromKey), toKey);
    }

    /** Converts between two units of the same category. */
    public double convert(double value, Unit from, Unit to) {
        if (!from.getCategory().equals(to.getCategory())) {
            throw new IllegalArgumentException("Cannot convert " + from.getCategory() + " to " + to.getCategory());
        }
        if (TEMPERATURE.equals(from.getCategory())) {
            return convertTemperature(value, from.getKey(), to.getKey());
        }
        return value * from.getToBase() / to.getToBase();
    }

    /** True only for temperatures outside -40..50 °C; other quantities are never extreme. */
    public boolean isExtreme(double value, Unit unit) {
        return TEMPERATURE.equals(unit.getCategory()) && isExtremeTemperature(toCelsius(value, unit.getKey()));
    }
}
