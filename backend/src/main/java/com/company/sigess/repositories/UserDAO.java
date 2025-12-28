package com.company.sigess.repositories;
import com.company.sigess.models.DTO.UserDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UserRepository {

    private  Connection connection;

    public UserRepository() {
        this.connection = DBConn
                .getInstance()
                .getConnection();
    }

    public List<UserDTO> findAll() {

        List<UserDTO> users = new ArrayList<>();

        String sql = "SELECT * FROM users WHERE active = true";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                UserDTO user = new UserDTO(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("role_id")

                );
                users.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();   // 👈 IMPORTANT
            throw new RuntimeException("Error fetching users", e);
        }

        return users;
    }

}
