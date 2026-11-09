package com.example;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ConversionTest {

    private final Unit celsius = new Unit(1, "celsius", "temperature", "°C", 0, "Celsius", "");
    private final Unit fahrenheit = new Unit(2, "fahrenheit", "temperature", "°F", 0, "Fahrenheit", "");

    @Test
    public void testFullConstructorGetters() {
        LocalDateTime time = LocalDateTime.of(2026, 11, 9, 12, 30);
        Conversion c = new Conversion(7, 100, celsius, 212, fahrenheit, "fi", time);

        assertEquals(7, c.getId());
        assertEquals(100, c.getInputValue());
        assertSame(celsius, c.getFromUnit());
        assertEquals(212, c.getResultValue());
        assertSame(fahrenheit, c.getToUnit());
        assertEquals("fi", c.getLang());
        assertEquals(time, c.getCreatedAt());
    }

    @Test
    public void testNewConversionHasNoIdOrTimestamp() {
        Conversion c = new Conversion(32, fahrenheit, 0, celsius, "en");
        assertEquals(0, c.getId());
        assertNull(c.getCreatedAt());
        assertEquals("en", c.getLang());
    }

    @Test
    public void testToStringWithoutTimestamp() {
        assertEquals(String.format("%.2f °C → %.2f °F  [ja]", 100.0, 212.0),
                new Conversion(100, celsius, 212, fahrenheit, "ja").toString());
    }

    @Test
    public void testToStringWithTimestamp() {
        Conversion c = new Conversion(1, 100, celsius, 212, fahrenheit, "ar", LocalDateTime.of(2026, 11, 9, 8, 5));
        assertTrue(c.toString().startsWith("08:05  "));
        assertTrue(c.toString().endsWith("[ar]"));
    }
}
