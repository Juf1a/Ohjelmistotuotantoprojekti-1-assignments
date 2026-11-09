package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads units with names and descriptions in the requested language.
 * If a translation is missing, the English one is used (translation table approach).
 */
public class UnitDAO {

    public static final String DEFAULT_LANG = "en";

    private static final String SELECT = """
            SELECT u.id, u.unit_key, u.category, u.symbol, u.to_base,
                   COALESCE(t.name, d.name, u.unit_key) AS name,
                   COALESCE(t.description, d.description, '') AS description
            FROM unit u
            LEFT JOIN unit_translation t ON t.unit_id = u.id AND t.lang = ?
            LEFT JOIN unit_translation d ON d.unit_id = u.id AND d.lang = '""" + DEFAULT_LANG + "'\n";

    public List<Unit> findAll(String lang) throws SQLException {
        return query(SELECT + "ORDER BY u.id", lang);
    }

    public List<Unit> findByCategory(String category, String lang) throws SQLException {
        return query(SELECT + "WHERE u.category = ? ORDER BY u.id", lang, category);
    }

    public Unit findByKey(String key, String lang) throws SQLException {
        List<Unit> units = query(SELECT + "WHERE u.unit_key = ?", lang, key);
        return units.isEmpty() ? null : units.get(0);
    }

    /** Categories in the order they were first defined (temperature, speed, distance, time). */
    public List<String> findCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT category FROM unit GROUP BY category ORDER BY MIN(id)");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        }
        return categories;
    }

    private List<Unit> query(String sql, String... params) throws SQLException {
        List<Unit> units = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setString(i + 1, params[i]);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    units.add(new Unit(rs.getInt("id"), rs.getString("unit_key"), rs.getString("category"),
                            rs.getString("symbol"), rs.getDouble("to_base"),
                            rs.getString("name"), rs.getString("description")));
                }
            }
        }
        return units;
    }
}
