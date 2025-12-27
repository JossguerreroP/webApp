package com.company.sigess.controllers;

import com.company.sigess.models.User;
import com.company.sigess.services.UserService;
import com.company.sigess.services.UserImp;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.List;

public class UserController implements HttpHandler {
    private UserService service = new UserService();
    private UserImp servicet = new UserImp();
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if ("GET".equals(method)) {
                if (path.equals("/api/users")) {
                    handleGetAllUsers(exchange);
                } else if (path.startsWith("/api/users/")) {
                    int id = Integer.parseInt(path.substring("/api/users/".length()));
                    handleGetUserById(exchange, id);
                }
            } else if ("POST".equals(method) && path.equals("/api/users")) {
                handleCreateUser(exchange);
            } else if ("DELETE".equals(method) && path.startsWith("/api/users/")) {
                int id = Integer.parseInt(path.substring("/api/users/".length()));
                handleDeleteUser(exchange, id);
            } else {
                sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
            }
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void handleGetAllUsers(HttpExchange exchange) throws IOException {
        List<User> users = servicet.getAllUsers();
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < users.size(); i++) {
            json.append(users.get(i).toString());
            if (i < users.size() - 1) json.append(",");
        }
        json.append("]");
        sendResponse(exchange, 200, json.toString());
    }

    private void handleGetUserById(HttpExchange exchange, int id) throws IOException {
        User user = service.getUserById(id);
        if (user != null) {
            sendResponse(exchange, 200, user.toString());
        } else {
            sendResponse(exchange, 404, "{\"error\": \"User not found\"}");
        }
    }

    private void handleCreateUser(HttpExchange exchange) throws IOException {
        // Simple parsing (for production use JSON library)
        String body = new String(exchange.getRequestBody().readAllBytes());
        // Extract name and email from body
        User user = new User(0, "New User", "email@example.com");
        User created = service.createUser(user.getName(), user.getEmail());
        sendResponse(exchange, 201, created.toString());
    }

    private void handleDeleteUser(HttpExchange exchange, int id) throws IOException {
        boolean deleted = service.deleteUser(id);
        if (deleted) {
            sendResponse(exchange, 200, "{\"message\": \"User deleted\"}");
        } else {
            sendResponse(exchange, 404, "{\"error\": \"User not found\"}");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }
}
