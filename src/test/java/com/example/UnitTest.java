package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTest {

    private final Unit unit = new Unit(5, "kilometre_per_hour", "speed", "km/h", 0.27,
            "キロメートル毎時", "道路の速度でよく使われる単位。");

    @Test
    public void testGetters() {
        assertEquals(5, unit.getId());
        assertEquals("kilometre_per_hour", unit.getKey());
        assertEquals("speed", unit.getCategory());
        assertEquals("km/h", unit.getSymbol());
        assertEquals(0.27, unit.getToBase());
        assertEquals("キロメートル毎時", unit.getName());
        assertEquals("道路の速度でよく使われる単位。", unit.getDescription());
    }

    @Test
    public void testToStringShowsLocalizedNameAndSymbol() {
        assertEquals("キロメートル毎時 (km/h)", unit.toString());
    }
}
