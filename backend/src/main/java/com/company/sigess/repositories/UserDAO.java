package com.company.sigess.repositories;
import com.company.sigess.models.DTO.UserDTO;
import java.sql.*;
import java.util.List;

public class UserDAO {
    private  Connection connection;
    public UserDAO() {
    }

    private Connection getConnection() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            this.connection = DBConn.getInstance().getConnection();
        }
        return this.connection;
    }

    public List<UserDTO> findAll() {
        String sql = "SELECT u.id, u.username, r.name as role_name " +
                     "FROM users u JOIN roles r ON u.role_id = r.id";
        java.util.List<UserDTO> users = new java.util.ArrayList<>();
        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(new UserDTO(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("role_name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }


    public UserDTO findById(int id) {
        String sql = "SELECT u.id, u.username, r.name as role_name " +
                     "FROM users u JOIN roles r ON u.role_id = r.id WHERE u.id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserDTO(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("role_name")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
