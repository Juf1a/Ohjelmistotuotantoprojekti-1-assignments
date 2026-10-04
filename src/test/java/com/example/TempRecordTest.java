package com.example;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TempRecordTest {

    private final TemperatureUnit celsius = new TemperatureUnit(1, "Celsius", "°C");
    private final TemperatureUnit fahrenheit = new TemperatureUnit(2, "Fahrenheit", "°F");

    @Test
    public void testFullConstructorGetters() {
        LocalDateTime time = LocalDateTime.of(2026, 10, 4, 12, 0);
        TempRecord record = new TempRecord(5, 100, celsius, 212, fahrenheit, time);

        assertEquals(5, record.getId());
        assertEquals(100, record.getInputValue());
        assertSame(celsius, record.getFromUnit());
        assertEquals(212, record.getResultValue());
        assertSame(fahrenheit, record.getToUnit());
        assertEquals(time, record.getCreatedAt());
    }

    @Test
    public void testNewRecordConstructorHasNoIdOrTimestamp() {
        TempRecord record = new TempRecord(32, fahrenheit, 0, celsius);

        assertEquals(0, record.getId());
        assertNull(record.getCreatedAt());
        assertEquals(32, record.getInputValue());
        assertEquals(0, record.getResultValue());
    }

    @Test
    public void testToString() {
        TempRecord record = new TempRecord(100, celsius, 212, fahrenheit);
        assertEquals("100.00 °C → 212.00 °F", record.toString());
    }
}
