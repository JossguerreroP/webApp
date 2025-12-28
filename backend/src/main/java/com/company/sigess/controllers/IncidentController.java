package com.company.sigess.controllers;

import com.company.sigess.models.DTO.IncidentDTO;
import com.company.sigess.services.IncidentServiceImp;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class IncidentController implements HttpHandler {

    private IncidentServiceImp service = new IncidentServiceImp();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if ("GET".equals(method)) {

                if (path.equals("/api/incidents")) {
                    handleGetAllIncidents(exchange);
                }
                else if (path.matches("/api/incidents/\\d+")) {
                    handleGetIncidentById(exchange);
                }
                else {
                    sendResponse(exchange, 404, "{\"error\":\"Endpoint not found\"}");
                }

            } else if ("POST".equals(method)) {

                if (path.equals("/api/incidents")) {
                    handleCreateIncident(exchange);
                }
                else {
                    sendResponse(exchange, 404, "{\"error\":\"Endpoint not found\"}");
                }

            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }

        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ================= HANDLERS =================

    private void handleGetAllIncidents(HttpExchange exchange) throws IOException {
        List<IncidentDTO> incidents = service.getAllIncidents();

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < incidents.size(); i++) {
            json.append(incidents.get(i).toString());
            if (i < incidents.size() - 1) json.append(",");
        }
        json.append("]");

        sendResponse(exchange, 200, json.toString());
    }

    private void handleGetIncidentById(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String idStr = path.substring(path.lastIndexOf("/") + 1);
        int id = Integer.parseInt(idStr);

        IncidentDTO incident = service.getIncidentById(id);

        if (incident == null) {
            sendResponse(exchange, 404, "{\"error\":\"Incident not found\"}");
            return;
        }

        sendResponse(exchange, 200, incident.toString());
    }

    private void handleCreateIncident(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange.getRequestBody());

        IncidentDTO incident = IncidentDTO.fromJson(body);
        IncidentDTO created = service.createIncident(incident);

        sendResponse(exchange, 201, created.toString());
    }

    // ================= UTILS =================

    private String readRequestBody(InputStream is) throws IOException {
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }
}
