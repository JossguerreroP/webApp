package com.company.sigess;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.company.sigess.controllers.UserController;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Route to controllers
        server.createContext("/api/users", new UserController());
        //server.createContext("/api/health", this::handleHealth);

        server.setExecutor(null);
        server.start();
        System.out.println("Server running on http://localhost:" + PORT);
    }

    private void handleHealth(HttpExchange exchange) throws IOException {
        String response = "{\"status\": \"UP\"}";
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }
}