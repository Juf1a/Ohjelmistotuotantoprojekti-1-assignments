package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TemperatureUnitTest {

    private final TemperatureUnit unit = new TemperatureUnit(1, "Celsius", "°C");

    @Test
    public void testGetters() {
        assertEquals(1, unit.getId());
        assertEquals("Celsius", unit.getName());
        assertEquals("°C", unit.getSymbol());
    }

    @Test
    public void testToString() {
        assertEquals("Celsius (°C)", unit.toString());
    }
}
