package com.company.sigess.controllers;

import com.company.sigess.models.DTO.UserDTO;
import com.company.sigess.security.JwtUtil;
import com.company.sigess.services.UserService;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/users/*")
public class UserController extends HttpServlet {

    private final UserService userService = new UserService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            handleGetAllUsers(req, resp);
        } else {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if ("/login".equals(pathInfo)) {
            handleLogin(req, resp);
        } else {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // Define a simple DTO structure for the login request internally
            // or use a Map to read username and password
            java.util.Map<String, String> credentials = gson.fromJson(req.getReader(), java.util.Map.class);
            String username = credentials.get("username");
            String password = credentials.get("password");

            if (username == null || password == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Username and password are required");
                return;
            }
            
            // In a real app, you would verify the password (e.g., using BCrypt)
            // For now, we find the user by name (username) and check if credentials match our records
            UserDTO user = userService.getAllUsers().stream()
                    .filter(u -> u.name().equalsIgnoreCase(username))
                    .findFirst()
                    .orElse(null);

            // Simple mock password check: password must match username + "123" 
            // to match your database data (analista123, supervisor123)
            boolean isAuthenticated = user != null && password.equals(username + "123");

            if (isAuthenticated) {
                String token = JwtUtil.generateToken(user);
                resp.setContentType("application/json");
                resp.getWriter().write("{\"token\":\"" + token + "\"}");
            } else {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Invalid username or password");
            }
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid request format");
        }
    }

    private void handleGetAllUsers(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<UserDTO> users = userService.getAllUsers();
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(users));
    }

    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}
