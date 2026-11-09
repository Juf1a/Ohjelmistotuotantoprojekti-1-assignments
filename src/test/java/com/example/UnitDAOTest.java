package com.example;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UnitDAOTest {

    /** A unit that only has an English translation, used to test the fallback. */
    private static final int TEST_UNIT_ID = 9001;

    private final UnitDAO dao = new UnitDAO();

    @BeforeAll
    static void setUp() throws Exception {
        DatabaseTestSupport.assumeDatabaseAvailable();
        try (Connection conn = DBConnection.getConnection()) {
            deleteTestUnit(conn);
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("INSERT INTO unit (id, unit_key, category, symbol, to_base) "
                        + "VALUES (" + TEST_UNIT_ID + ", 'test_furlong', 'test', 'fur', 201.168)");
                stmt.executeUpdate("INSERT INTO unit_translation (unit_id, lang, name, description) "
                        + "VALUES (" + TEST_UNIT_ID + ", 'en', 'furlong', 'Only translated to English.')");
            }
        }
    }

    @AfterAll
    static void tearDown() throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            deleteTestUnit(conn);
        }
    }

    private static void deleteTestUnit(Connection conn) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM unit WHERE id = ?")) {
            stmt.setInt(1, TEST_UNIT_ID);
            stmt.executeUpdate();
        }
    }

    @Test
    public void testTemperatureUnitsInEnglish() throws Exception {
        List<String> names = dao.findByCategory("temperature", "en").stream().map(Unit::getName).toList();
        assertEquals(List.of("Celsius", "Fahrenheit", "Kelvin"), names);
    }

    @Test
    public void testJapaneseNamesAndDescription() throws Exception {
        Unit celsius = dao.findByKey("celsius", "ja");
        assertEquals("摂氏", celsius.getName());
        assertEquals("°C", celsius.getSymbol());
        assertEquals("水は0 °Cで凍り、100 °Cで沸騰します。", celsius.getDescription());
    }

    @Test
    public void testArabicName() throws Exception {
        assertEquals("كيلومتر في الساعة", dao.findByKey("kilometre_per_hour", "ar").getName());
    }

    @Test
    public void testEveryUnitTranslatedToAllFiveLanguages() throws Exception {
        List<Unit> english = dao.findAll("en");
        for (String lang : new String[] {"fi", "sv", "ja", "ar"}) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT COUNT(*) FROM unit_translation WHERE lang = ? AND unit_id <> ?")) {
                stmt.setString(1, lang);
                stmt.setInt(2, TEST_UNIT_ID);
                var rs = stmt.executeQuery();
                rs.next();
                assertEquals(english.size() - 1, rs.getInt(1), lang);
            }
        }
    }

    @Test
    public void testFallsBackToEnglishWhenTranslationMissing() throws Exception {
        Unit unit = dao.findByKey("test_furlong", "fi");
        assertEquals("furlong", unit.getName());
        assertEquals("Only translated to English.", unit.getDescription());
    }

    @Test
    public void testFallsBackToEnglishForUnknownLanguage() throws Exception {
        assertEquals("Celsius", dao.findByKey("celsius", "xx").getName());
    }

    @Test
    public void testSpeedDistanceAndTimeUnitsExist() throws Exception {
        assertEquals(3, dao.findByCategory("speed", "en").size());
        assertEquals(3, dao.findByCategory("distance", "en").size());
        assertEquals(3, dao.findByCategory("time", "en").size());
        assertEquals(3600, dao.findByKey("hour", "en").getToBase());
    }

    @Test
    public void testCategoriesInDefinitionOrder() throws Exception {
        assertEquals(List.of("temperature", "speed", "distance", "time"),
                dao.findCategories().subList(0, 4));
    }

    @Test
    public void testUnknownKeyReturnsNull() throws Exception {
        assertNull(dao.findByKey("rankine", "en"));
    }
}
