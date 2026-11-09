package com.example;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class LanguageTest {

    @Test
    public void testCodesMatchDatabaseLangValues() {
        assertEquals("en", Language.ENGLISH.getCode());
        assertEquals("fi", Language.FINNISH.getCode());
        assertEquals("sv", Language.SWEDISH.getCode());
        assertEquals("ja", Language.JAPANESE.getCode());
        assertEquals("ar", Language.ARABIC.getCode());
    }

    @Test
    public void testLocalesMatchBundleFiles() {
        assertEquals(new Locale("fi", "FI"), Language.FINNISH.getLocale());
        assertEquals(new Locale("ja", "JP"), Language.JAPANESE.getLocale());
        assertEquals(new Locale("ar", "SA"), Language.ARABIC.getLocale());
    }

    @Test
    public void testOnlyArabicIsRightToLeft() {
        for (Language language : Language.values()) {
            assertEquals(language == Language.ARABIC, language.isRightToLeft(), language.name());
        }
    }

    @Test
    public void testFromLocale() {
        assertEquals(Language.SWEDISH, Language.fromLocale(new Locale("sv", "SE")));
        assertEquals(Language.JAPANESE, Language.fromLocale(Locale.JAPANESE));
        assertEquals(Language.ENGLISH, Language.fromLocale(Locale.GERMAN));
    }

    @Test
    public void testToStringIsNativeName() {
        assertEquals("Suomi", Language.FINNISH.toString());
        assertEquals("日本語", Language.JAPANESE.toString());
        assertEquals("العربية", Language.ARABIC.toString());
    }
}
