package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConversionDAO {

    private final UnitDAO unitDAO;

    public ConversionDAO() {
        this(new UnitDAO());
    }

    public ConversionDAO(UnitDAO unitDAO) {
        this.unitDAO = unitDAO;
    }

    public void save(Conversion conversion) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO conversion_record (input_value, from_unit_id, result_value, to_unit_id, lang) "
                             + "VALUES (?, ?, ?, ?, ?)")) {
            stmt.setDouble(1, conversion.getInputValue());
            stmt.setInt(2, conversion.getFromUnit().getId());
            stmt.setDouble(3, conversion.getResultValue());
            stmt.setInt(4, conversion.getToUnit().getId());
            stmt.setString(5, conversion.getLang());
            stmt.executeUpdate();
        }
    }

    /** Newest conversions first, with unit names in the given display language. */
    public List<Conversion> findRecent(int limit, String displayLang) throws SQLException {
        Map<Integer, Unit> units = unitDAO.findAll(displayLang).stream()
                .collect(Collectors.toMap(Unit::getId, Function.identity()));
        List<Conversion> conversions = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT id, input_value, from_unit_id, result_value, to_unit_id, lang, created_at "
                             + "FROM conversion_record ORDER BY id DESC LIMIT ?")) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Timestamp created = rs.getTimestamp("created_at");
                    conversions.add(new Conversion(rs.getInt("id"), rs.getDouble("input_value"),
                            units.get(rs.getInt("from_unit_id")), rs.getDouble("result_value"),
                            units.get(rs.getInt("to_unit_id")), rs.getString("lang"),
                            created == null ? null : created.toLocalDateTime()));
                }
            }
        }
        return conversions;
    }
}
