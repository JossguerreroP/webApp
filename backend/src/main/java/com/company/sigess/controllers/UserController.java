package com.company.sigess.controllers;

import com.company.sigess.models.DTO.UserDTO;
import com.company.sigess.services.UserService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.List;

public class UserController implements HttpHandler {


    private UserService servicet = new UserService();
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if ("GET".equals(method)) {
                if (path.equals("/api/users")) {
                    handleGetAllUsers(exchange);
                }
            }
            else {
                sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
            }
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void handleGetAllUsers(HttpExchange exchange) throws IOException {
        List<UserDTO> users = servicet.getAllUsers();
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < users.size(); i++) {
            json.append(users.get(i).toString());
            if (i < users.size() - 1) json.append(",");
        }
        json.append("]");
        sendResponse(exchange, 200, json.toString());
    }



    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }
}
