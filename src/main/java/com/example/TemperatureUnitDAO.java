package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TemperatureUnitDAO {

    public List<TemperatureUnit> findAll() throws SQLException {
        List<TemperatureUnit> units = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT id, name, symbol FROM temperature_unit ORDER BY id");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                units.add(new TemperatureUnit(rs.getInt("id"), rs.getString("name"), rs.getString("symbol")));
            }
        }
        return units;
    }

    public TemperatureUnit findByName(String name) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT id, name, symbol FROM temperature_unit WHERE name = ?")) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new TemperatureUnit(rs.getInt("id"), rs.getString("name"), rs.getString("symbol"));
                }
            }
        }
        return null;
    }
}
