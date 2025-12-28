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
            UserDTO loginRequest = gson.fromJson(req.getReader(), UserDTO.class);
            // In a real app, you would verify credentials here.
            // For this implementation, we'll assume the user exists if they send a valid JSON.
            // Let's try to find the user by ID or Name if possible, or just mock it for demonstration.
            
            UserDTO user = userService.getAllUsers().stream()
                    .filter(u -> u.name().equalsIgnoreCase(loginRequest.name()))
                    .findFirst()
                    .orElse(null);

            if (user != null) {
                String token = JwtUtil.generateToken(user);
                resp.setContentType("application/json");
                resp.getWriter().write("{\"token\":\"" + token + "\"}");
            } else {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
            }
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
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
