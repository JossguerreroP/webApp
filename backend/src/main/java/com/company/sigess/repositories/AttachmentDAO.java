package com.company.sigess.repositories;

import com.company.sigess.models.DTO.AttachmentDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttachmentDAO {

    private Connection getConnection() throws SQLException {
        return DBConn.getInstance().getConnection();
    }



    public AttachmentDTO create(AttachmentDTO attachment) {
        String sql = "INSERT INTO incident_attachments (incident_id, original_filename, stored_filename, file_size, mime_type, description, uploaded_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id, uploaded_at";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, attachment.getIncidentId());
            stmt.setString(2, attachment.getOriginalFilename());
            stmt.setString(3, attachment.getStoredFilename());
            stmt.setLong(4, attachment.getFileSize());
            stmt.setString(5, attachment.getMimeType());
            stmt.setString(6, attachment.getDescription());
            stmt.setInt(7, attachment.getUploadedBy());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    attachment.setId(rs.getInt("id"));
                    attachment.setUploadedAt(rs.getTimestamp("uploaded_at").toLocalDateTime());
                }
            }
            return attachment;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving attachment to DB", e);
        }
    }

    public List<AttachmentDTO> findByIncidentId(int incidentId) {
        List<AttachmentDTO> attachments = new ArrayList<>();
        String sql = "SELECT * FROM incident_attachments WHERE incident_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, incidentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attachments.add(mapResultSetToDTO(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attachments;
    }

    private AttachmentDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        AttachmentDTO dto = new AttachmentDTO();
        dto.setId(rs.getInt("id"));
        dto.setIncidentId(rs.getInt("incident_id"));
        dto.setOriginalFilename(rs.getString("original_filename"));
        dto.setStoredFilename(rs.getString("stored_filename"));
        dto.setFileSize(rs.getLong("file_size"));
        dto.setMimeType(rs.getString("mime_type"));
        dto.setDescription(rs.getString("description"));
        dto.setUploadedBy(rs.getInt("uploaded_by"));
        dto.setUploadedAt(rs.getTimestamp("uploaded_at").toLocalDateTime());
        return dto;
    }
}
