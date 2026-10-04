package com.example;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class DBConnectionTest {

    @Test
    public void testUrlPointsToMariaDB() {
        assertTrue(DBConnection.url().startsWith("jdbc:mariadb://"));
    }

    @Test
    public void testUserIsNotEmpty() {
        assertFalse(DBConnection.user().isEmpty());
    }

    @Test
    public void testPasswordIsNotNull() {
        assertNotNull(DBConnection.password());
    }

    @Test
    public void testGetConnectionReturnsValidConnection() throws Exception {
        DatabaseTestSupport.assumeDatabaseAvailable();
        try (Connection conn = DBConnection.getConnection()) {
            assertTrue(conn.isValid(2));
        }
    }
}
