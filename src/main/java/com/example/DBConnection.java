package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    static String url() {
        return env("DB_URL", "jdbc:mariadb://localhost:3306/temperature_db");
    }

    static String user() {
        return env("DB_USER", "root");
    }

    static String password() {
        return env("DB_PASSWORD", "");
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isEmpty() ? fallback : value;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url(), user(), password());
    }
}
