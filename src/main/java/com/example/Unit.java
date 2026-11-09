package com.example;

/** A unit with its name and description already localized for one language. */
public class Unit {

    private final int id;
    private final String key;
    private final String category;
    private final String symbol;
    private final double toBase;
    private final String name;
    private final String description;

    public Unit(int id, String key, String category, String symbol, double toBase,
                String name, String description) {
        this.id = id;
        this.key = key;
        this.category = category;
        this.symbol = symbol;
        this.toBase = toBase;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getCategory() {
        return category;
    }

    public String getSymbol() {
        return symbol;
    }

    /** Factor to the category's base unit (m/s, m, s). Unused for temperature. */
    public double getToBase() {
        return toBase;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name + " (" + symbol + ")";
    }
}
