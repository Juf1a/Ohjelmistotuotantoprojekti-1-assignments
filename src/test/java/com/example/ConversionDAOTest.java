package com.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ConversionDAOTest {

    private final UnitDAO unitDAO = new UnitDAO();
    private final ConversionDAO dao = new ConversionDAO(unitDAO);
    private final List<Integer> createdIds = new ArrayList<>();

    @BeforeAll
    static void checkDatabase() {
        DatabaseTestSupport.assumeDatabaseAvailable();
    }

    @AfterEach
    void deleteCreated() throws Exception {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM conversion_record WHERE id = ?")) {
            for (int id : createdIds) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
        }
    }

    @Test
    public void testSaveThenFindRecentInAnotherLanguage() throws Exception {
        Unit celsius = unitDAO.findByKey("celsius", "en");
        Unit fahrenheit = unitDAO.findByKey("fahrenheit", "en");

        dao.save(new Conversion(-123.45, celsius, -190.21, fahrenheit, "fi"));

        Conversion latest = new ConversionDAO().findRecent(1, "ja").get(0);
        createdIds.add(latest.getId());

        assertEquals(-123.45, latest.getInputValue());
        assertEquals(-190.21, latest.getResultValue());
        assertEquals("fi", latest.getLang());
        assertEquals("摂氏", latest.getFromUnit().getName());
        assertEquals("華氏", latest.getToUnit().getName());
        assertNotNull(latest.getCreatedAt());
    }

    @Test
    public void testFindRecentRespectsLimitAndNewestFirst() throws Exception {
        Unit km = unitDAO.findByKey("kilometre", "en");
        Unit mile = unitDAO.findByKey("mile", "en");

        dao.save(new Conversion(1, km, 0.62, mile, "en"));
        dao.save(new Conversion(2, km, 1.24, mile, "ar"));

        List<Conversion> recent = dao.findRecent(2, "en");
        recent.forEach(c -> createdIds.add(c.getId()));

        assertEquals(2, recent.size());
        assertEquals(2, recent.get(0).getInputValue());
        assertEquals("ar", recent.get(0).getLang());
        assertEquals(1, recent.get(1).getInputValue());
    }
}
