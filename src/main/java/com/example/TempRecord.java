package com.example;

import java.time.LocalDateTime;

public class TempRecord {

    private final int id;
    private final double inputValue;
    private final TemperatureUnit fromUnit;
    private final double resultValue;
    private final TemperatureUnit toUnit;
    private final LocalDateTime createdAt;

    public TempRecord(int id, double inputValue, TemperatureUnit fromUnit,
                      double resultValue, TemperatureUnit toUnit, LocalDateTime createdAt) {
        this.id = id;
        this.inputValue = inputValue;
        this.fromUnit = fromUnit;
        this.resultValue = resultValue;
        this.toUnit = toUnit;
        this.createdAt = createdAt;
    }

    public TempRecord(double inputValue, TemperatureUnit fromUnit, double resultValue, TemperatureUnit toUnit) {
        this(0, inputValue, fromUnit, resultValue, toUnit, null);
    }

    public int getId() {
        return id;
    }

    public double getInputValue() {
        return inputValue;
    }

    public TemperatureUnit getFromUnit() {
        return fromUnit;
    }

    public double getResultValue() {
        return resultValue;
    }

    public TemperatureUnit getToUnit() {
        return toUnit;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return String.format("%.2f %s → %.2f %s",
                inputValue, fromUnit.getSymbol(), resultValue, toUnit.getSymbol());
    }
}
