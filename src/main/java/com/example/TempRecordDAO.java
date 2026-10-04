package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TempRecordDAO {

    public void save(TempRecord record) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO temp_record (input_value, from_unit_id, result_value, to_unit_id) VALUES (?, ?, ?, ?)")) {
            stmt.setDouble(1, record.getInputValue());
            stmt.setInt(2, record.getFromUnit().getId());
            stmt.setDouble(3, record.getResultValue());
            stmt.setInt(4, record.getToUnit().getId());
            stmt.executeUpdate();
        }
    }

    public List<TempRecord> findRecent(int limit) throws SQLException {
        String sql = "SELECT r.id, r.input_value, r.result_value, r.created_at, "
                + "f.id AS f_id, f.name AS f_name, f.symbol AS f_symbol, "
                + "t.id AS t_id, t.name AS t_name, t.symbol AS t_symbol "
                + "FROM temp_record r "
                + "JOIN temperature_unit f ON r.from_unit_id = f.id "
                + "JOIN temperature_unit t ON r.to_unit_id = t.id "
                + "ORDER BY r.id DESC LIMIT ?";
        List<TempRecord> records = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TemperatureUnit from = new TemperatureUnit(rs.getInt("f_id"), rs.getString("f_name"), rs.getString("f_symbol"));
                    TemperatureUnit to = new TemperatureUnit(rs.getInt("t_id"), rs.getString("t_name"), rs.getString("t_symbol"));
                    Timestamp created = rs.getTimestamp("created_at");
                    records.add(new TempRecord(rs.getInt("id"), rs.getDouble("input_value"), from,
                            rs.getDouble("result_value"), to, created == null ? null : created.toLocalDateTime()));
                }
            }
        }
        return records;
    }
}
