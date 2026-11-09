package com.example;

import java.util.Locale;

/** Languages offered in the selector. The code is also the lang value used in the database. */
public enum Language {
    ENGLISH(new Locale("en", "US"), "English"),
    FINNISH(new Locale("fi", "FI"), "Suomi"),
    SWEDISH(new Locale("sv", "SE"), "Svenska"),
    JAPANESE(new Locale("ja", "JP"), "日本語"),
    ARABIC(new Locale("ar", "SA"), "العربية");

    private final Locale locale;
    private final String label;

    Language(Locale locale, String label) {
        this.locale = locale;
        this.label = label;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getCode() {
        return locale.getLanguage();
    }

    public boolean isRightToLeft() {
        return this == ARABIC;
    }

    public static Language fromLocale(Locale locale) {
        for (Language language : values()) {
            if (language.getCode().equals(locale.getLanguage())) {
                return language;
            }
        }
        return ENGLISH;
    }

    @Override
    public String toString() {
        return label;
    }
}
