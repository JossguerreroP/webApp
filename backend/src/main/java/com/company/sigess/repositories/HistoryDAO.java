package com.company.sigess.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HistoryDAO {
    private Connection connection;

    public HistoryDAO() {
    }

    private Connection getConnection() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            this.connection = DBConn.getInstance().getConnection();
        }
        return this.connection;
    }

    public void logChange(int incidentId, int changedBy, String fieldName, String oldValue, String newValue) {
        String sql = "INSERT INTO incident_history (incident_id, changed_by, field_name, old_value, new_value, changed_at) " +
                     "VALUES (?, ?, ?, ?, ?, NOW())";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, incidentId);
            stmt.setInt(2, changedBy);
            stmt.setString(3, fieldName);
            stmt.setString(4, oldValue);
            stmt.setString(5, newValue);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al registrar historial de auditoría", e);
        }
    }
}
