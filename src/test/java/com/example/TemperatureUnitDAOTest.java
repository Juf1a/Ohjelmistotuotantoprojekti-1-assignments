package com.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TemperatureUnitDAOTest {

    private final TemperatureUnitDAO dao = new TemperatureUnitDAO();

    @BeforeAll
    static void checkDatabase() {
        DatabaseTestSupport.assumeDatabaseAvailable();
    }

    @Test
    public void testFindAllContainsCelsiusAndFahrenheit() throws Exception {
        List<String> names = dao.findAll().stream().map(TemperatureUnit::getName).toList();
        assertTrue(names.contains("Celsius"));
        assertTrue(names.contains("Fahrenheit"));
    }

    @Test
    public void testFindByNameReturnsUnit() throws Exception {
        TemperatureUnit celsius = dao.findByName("Celsius");
        assertNotNull(celsius);
        assertEquals("°C", celsius.getSymbol());
        assertTrue(celsius.getId() > 0);
    }

    @Test
    public void testFindByNameReturnsNullForUnknownUnit() throws Exception {
        assertNull(dao.findByName("Kelvin"));
    }
}
