package com.company.sigess.controllers;

import com.company.sigess.models.DTO.IncidentCriteria;
import com.company.sigess.models.DTO.IncidentDTO;
import com.company.sigess.models.DTO.IncidentReportDTO;
import com.company.sigess.services.IncidentService;
import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/api/incidents/*")
public class IncidentController extends HttpServlet {

    private final IncidentService service = new IncidentService();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                @Override
                public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                }
            })
            .registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                @Override
                public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
            })
            .setPrettyPrinting()
            .create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        System.out.println("[DEBUG_LOG] GET pathInfo: " + pathInfo);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                handleGetAllIncidents(req, resp);
            } else if (pathInfo.equals("/reports")) {
                handleGetReports(req, resp);
            } else if (pathInfo.matches("/\\d+")) {
                handleGetIncidentById(req, resp);
            } else {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found: " + pathInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            IncidentDTO incident = gson.fromJson(req.getReader(), IncidentDTO.class);
            IncidentDTO created = service.createIncident(incident);
            sendResponse(resp, HttpServletResponse.SC_CREATED, created);
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid ID");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            IncidentDTO incident = gson.fromJson(req.getReader(), IncidentDTO.class);
            incident.setId(id);
            
            int userId = -1; 
            if (req.getParameter("userId") != null) {
                userId = Integer.parseInt(req.getParameter("userId"));
            }

            IncidentDTO updated = service.updateIncident(incident, userId);
            sendResponse(resp, HttpServletResponse.SC_OK, updated);
        } catch (Exception e) {
            e.printStackTrace();
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void handleGetReports(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        IncidentReportDTO report = service.getIncidentReport();
        sendResponse(resp, HttpServletResponse.SC_OK, report);
    }

    private void handleGetAllIncidents(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        IncidentCriteria criteria = new IncidentCriteria();

        if (req.getParameter("status") != null) criteria.setStatus(req.getParameter("status"));
        if (req.getParameter("level") != null) criteria.setLevel(req.getParameter("level"));
        if (req.getParameter("areaId") != null) criteria.setAreaId(Integer.parseInt(req.getParameter("areaId")));
        if (req.getParameter("responsibleId") != null) criteria.setResponsibleId(Integer.parseInt(req.getParameter("responsibleId")));
        if (req.getParameter("searchTerm") != null) criteria.setSearchTerm(req.getParameter("searchTerm"));
        
        if (req.getParameter("page") != null) criteria.setPage(Integer.parseInt(req.getParameter("page")));
        if (req.getParameter("size") != null) criteria.setSize(Integer.parseInt(req.getParameter("size")));
        if (req.getParameter("sortBy") != null) criteria.setSortBy(req.getParameter("sortBy"));
        if (req.getParameter("sortOrder") != null) criteria.setSortOrder(req.getParameter("sortOrder"));

        if (req.getParameter("startDate") != null) criteria.setStartDate(LocalDateTime.parse(req.getParameter("startDate")));
        if (req.getParameter("endDate") != null) criteria.setEndDate(LocalDateTime.parse(req.getParameter("endDate")));

        List<IncidentDTO> incidents = service.getAllIncidents(criteria);
        sendResponse(resp, HttpServletResponse.SC_OK, incidents);
    }

    private void handleGetIncidentById(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing ID");
            return;
        }
        
        String idStr = pathInfo.substring(1);
        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format: " + idStr);
            return;
        }

        IncidentDTO incident = service.getIncidentById(id);

        if (incident == null) {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Incident with ID " + id + " not found");
            return;
        }

        sendResponse(resp, HttpServletResponse.SC_OK, incident);
    }

    private void sendResponse(HttpServletResponse resp, int status, Object data) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(data));
    }

    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.getWriter().write("{\"error\":\"" + message + "\"}");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid ID");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            
            int userId = -1; 
            if (req.getParameter("userId") != null) {
                userId = Integer.parseInt(req.getParameter("userId"));
            }

            boolean closed = service.deleteIncident(id, userId);

            if (closed) {
                sendResponse(resp, HttpServletResponse.SC_OK, new MessageResponse("Incident closed successfully"));
            } else {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Incident not found");
            }
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private static class MessageResponse {
        private final String message;
        public MessageResponse(String message) { this.message = message; }
    }
}
