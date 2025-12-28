package com.company.sigess.repositories;
import com.company.sigess.models.DTO.UserDTO;
import java.sql.*;
import java.util.ArrayList;
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

    public List<UserDTO> ok(){
       return null;
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
