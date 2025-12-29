package com.company.sigess.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.company.sigess.models.DTO.HistoryDTO;

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

    public List<HistoryDTO> getHistoryByIncidentId(int incidentId) {
        List<HistoryDTO> history = new ArrayList<>();
        String sql = "SELECT id, incident_id, changed_by, field_name, old_value, new_value, changed_at " +
                     "FROM incident_history " +
                     "WHERE incident_id = ? " +
                     "ORDER BY changed_at DESC";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, incidentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    HistoryDTO dto = new HistoryDTO();
                    dto.setId(rs.getInt("id"));
                    dto.setIncidentId(rs.getInt("incident_id"));
                    dto.setChangedBy(rs.getInt("changed_by"));
                    dto.setFieldName(rs.getString("field_name"));
                    dto.setOldValue(rs.getString("old_value"));
                    dto.setNewValue(rs.getString("new_value"));
                    dto.setChangedAt(rs.getTimestamp("changed_at").toLocalDateTime());
                    history.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al obtener historial de auditoría", e);
        }
        return history;
    }
}
