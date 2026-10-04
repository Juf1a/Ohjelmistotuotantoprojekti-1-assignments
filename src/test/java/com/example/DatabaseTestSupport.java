package com.example;

import java.sql.Connection;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

final class DatabaseTestSupport {

    private DatabaseTestSupport() {
    }

    static void assumeDatabaseAvailable() {
        boolean available;
        try (Connection conn = DBConnection.getConnection()) {
            available = conn.isValid(2);
        } catch (Exception e) {
            available = false;
        }
        assumeTrue(available, "MariaDB not reachable, skipping database tests");
    }
}
