package com.company.sigess.repositories;

import com.company.sigess.models.DTO.IncidentCriteria;
import com.company.sigess.models.DTO.IncidentDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IncidentDAO {
    private Connection connection;

    public IncidentDAO() {
    }

    private Connection getConnection() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            this.connection = DBConn.getInstance().getConnection();
        }
        return this.connection;
    }

    public List<IncidentDTO> findAll(IncidentCriteria criteria) {
        List<IncidentDTO> incidents = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM incidents WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (criteria != null) {
            if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
                sql.append(" AND status = ?");
                params.add(criteria.getStatus());
            }
            if (criteria.getLevel() != null && !criteria.getLevel().isEmpty()) {
                sql.append(" AND level = ?");
                params.add(criteria.getLevel());
            }
            if (criteria.getAreaId() != null && criteria.getAreaId() != 0) {
                sql.append(" AND area_id = ?");
                params.add(criteria.getAreaId());
            }
            if (criteria.getResponsibleId() != null && criteria.getResponsibleId() != 0) {
                sql.append(" AND responsible_id = ?");
                params.add(criteria.getResponsibleId());
            }
            if (criteria.getStartDate() != null) {
                sql.append(" AND created_at >= ?");
                params.add(Timestamp.valueOf(criteria.getStartDate()));
            }
            if (criteria.getEndDate() != null) {
                sql.append(" AND created_at <= ?");
                params.add(Timestamp.valueOf(criteria.getEndDate()));
            }
            if (criteria.getSearchTerm() != null && !criteria.getSearchTerm().isEmpty()) {
                sql.append(" AND (title ILIKE ? OR description ILIKE ?)");
                params.add("%" + criteria.getSearchTerm() + "%");
                params.add("%" + criteria.getSearchTerm() + "%");
            }

            // Ordenamiento seguro (validar campos permitidos)
            String sortBy = "created_at";
            if ("level".equalsIgnoreCase(criteria.getSortBy())) sortBy = "level";
            
            String order = "DESC";
            if ("ASC".equalsIgnoreCase(criteria.getSortOrder())) order = "ASC";
            
            sql.append(" ORDER BY ").append(sortBy).append(" ").append(order);
            
            // Paginación
            if (criteria.getSize() > 0) {
                sql.append(" LIMIT ? OFFSET ?");
                params.add(criteria.getSize());
                params.add(criteria.getOffset());
            }
        }

        try (PreparedStatement stmt = getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    incidents.add(mapResultSetToDTO(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al filtrar incidentes", e);
        }
        return incidents;
    }

    private IncidentDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        IncidentDTO dto = new IncidentDTO();
        dto.setId(rs.getInt("id"));
        dto.setTitle(rs.getString("title"));
        dto.setDescription(rs.getString("description"));
        dto.setType(rs.getString("type"));
        dto.setLevel(rs.getString("level"));
        dto.setStatus(rs.getString("status"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            dto.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            dto.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        dto.setResponsibleId(rs.getInt("responsible_id"));
        dto.setAreaId(rs.getInt("area_id"));
        dto.setCreatedBy(rs.getInt("created_by"));
        dto.setVersion(rs.getInt("version"));
        
        return dto;
    }

    public IncidentDTO create(IncidentDTO incident) {
        String sql = "INSERT INTO incidents (title, description, type, level, status, responsible_id, area_id, created_by, version) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1) RETURNING id, created_at, updated_at";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, incident.getTitle());
            stmt.setString(2, incident.getDescription());
            stmt.setString(3, incident.getType());
            stmt.setString(4, incident.getLevel());
            stmt.setString(5, incident.getStatus());
            stmt.setInt(6, incident.getResponsibleId());
            stmt.setInt(7, incident.getAreaId());
            stmt.setInt(8, incident.getCreatedBy());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    incident.setId(rs.getInt("id"));
                    incident.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    incident.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    incident.setVersion(1);
                }
            }
            return incident;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear incidente", e);
        }
    }

    public IncidentDTO findById(int id) {
        String sql = "SELECT * FROM incidents WHERE id = ?";
        System.out.println("[DEBUG_LOG] Executing findById for ID: " + id);
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("[DEBUG_LOG] Found incident ID: " + id);
                    return mapResultSetToDTO(rs);
                } else {
                    System.out.println("[DEBUG_LOG] Incident ID: " + id + " NOT FOUND in DB");
                }
            }
        } catch (SQLException e) {
            System.err.println("[DEBUG_LOG] SQLException in findById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public IncidentDTO update(IncidentDTO incident) {
        String sql = "UPDATE incidents SET title = ?, description = ?, type = ?, level = ?, status = ?, " +
                     "responsible_id = ?, area_id = ?, updated_at = NOW(), version = version + 1 " +
                     "WHERE id = ? AND version = ? RETURNING updated_at, version";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, incident.getTitle());
            stmt.setString(2, incident.getDescription());
            stmt.setString(3, incident.getType());
            stmt.setString(4, incident.getLevel());
            stmt.setString(5, incident.getStatus());
            stmt.setInt(6, incident.getResponsibleId());
            stmt.setInt(7, incident.getAreaId());
            stmt.setInt(8, incident.getId());
            stmt.setInt(9, incident.getVersion());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    incident.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    incident.setVersion(rs.getInt("version"));
                    return incident;
                } else {
                    // Verificar si el incidente existe pero la versión es incorrecta
                    IncidentDTO actual = findById(incident.getId());
                    if (actual == null) {
                        throw new RuntimeException("Incidente con ID " + incident.getId() + " no encontrado");
                    } else {
                        throw new RuntimeException("Error de concurrencia: La versión enviada (" + incident.getVersion() + 
                            ") no coincide con la versión en base de datos (" + actual.getVersion() + ")");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar incidente", e);
        }
    }
}
