package com.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TempRecordDAOTest {

    private final TempRecordDAO recordDAO = new TempRecordDAO();
    private final TemperatureUnitDAO unitDAO = new TemperatureUnitDAO();
    private final List<Integer> createdIds = new ArrayList<>();

    @BeforeAll
    static void checkDatabase() {
        DatabaseTestSupport.assumeDatabaseAvailable();
    }

    @AfterEach
    void deleteCreatedRecords() throws Exception {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM temp_record WHERE id = ?")) {
            for (int id : createdIds) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
        }
    }

    @Test
    public void testSaveThenFindRecentReturnsRecord() throws Exception {
        TemperatureUnit celsius = unitDAO.findByName("Celsius");
        TemperatureUnit fahrenheit = unitDAO.findByName("Fahrenheit");

        recordDAO.save(new TempRecord(-123.45, celsius, -190.21, fahrenheit));

        TempRecord latest = recordDAO.findRecent(1).get(0);
        createdIds.add(latest.getId());

        assertEquals(-123.45, latest.getInputValue());
        assertEquals(-190.21, latest.getResultValue());
        assertEquals("Celsius", latest.getFromUnit().getName());
        assertEquals("Fahrenheit", latest.getToUnit().getName());
        assertNotNull(latest.getCreatedAt());
    }

    @Test
    public void testFindRecentRespectsLimitAndNewestFirst() throws Exception {
        TemperatureUnit celsius = unitDAO.findByName("Celsius");
        TemperatureUnit fahrenheit = unitDAO.findByName("Fahrenheit");

        recordDAO.save(new TempRecord(1, celsius, 33.8, fahrenheit));
        recordDAO.save(new TempRecord(2, celsius, 35.6, fahrenheit));

        List<TempRecord> recent = recordDAO.findRecent(2);
        recent.forEach(r -> createdIds.add(r.getId()));

        assertEquals(2, recent.size());
        assertEquals(2, recent.get(0).getInputValue());
        assertEquals(1, recent.get(1).getInputValue());
    }
}
