package com.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** One saved conversion. */
public class Conversion {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    private final int id;
    private final double inputValue;
    private final Unit fromUnit;
    private final double resultValue;
    private final Unit toUnit;
    private final String lang;
    private final LocalDateTime createdAt;

    public Conversion(int id, double inputValue, Unit fromUnit, double resultValue, Unit toUnit,
                      String lang, LocalDateTime createdAt) {
        this.id = id;
        this.inputValue = inputValue;
        this.fromUnit = fromUnit;
        this.resultValue = resultValue;
        this.toUnit = toUnit;
        this.lang = lang;
        this.createdAt = createdAt;
    }

    public Conversion(double inputValue, Unit fromUnit, double resultValue, Unit toUnit, String lang) {
        this(0, inputValue, fromUnit, resultValue, toUnit, lang, null);
    }

    public int getId() {
        return id;
    }

    public double getInputValue() {
        return inputValue;
    }

    public Unit getFromUnit() {
        return fromUnit;
    }

    public double getResultValue() {
        return resultValue;
    }

    public Unit getToUnit() {
        return toUnit;
    }

    public String getLang() {
        return lang;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        String text = String.format("%.2f %s → %.2f %s  [%s]",
                inputValue, fromUnit.getSymbol(), resultValue, toUnit.getSymbol(), lang);
        return createdAt == null ? text : TIME.format(createdAt) + "  " + text;
    }
}
