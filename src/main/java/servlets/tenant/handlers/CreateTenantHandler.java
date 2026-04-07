package servlets.tenant.handlers;

import com.google.gson.Gson;
import dao.TenantRepository;
import models.Tenant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateTenantHandler {
    private static final Gson gson = new Gson();

    public static void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Map<String, String> requestBody = gson.fromJson(req.getReader(), Map.class);

            String name = requestBody.get("name");
            String description = requestBody.get("description");

            if (name == null || name.trim().isEmpty()) {
                sendError(resp, 400, "Tenant name is required");
                return;
            }

            Tenant newTenant = new Tenant(name, description);
            newTenant.setId(UUID.randomUUID().toString());

            TenantRepository.create(newTenant);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tenant created successfully");
            response.put("tenant", newTenant);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson(response));

            System.out.println("Tenant created successfully: " + newTenant.getId());

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
            sendError(resp, 500, "Database error: " + e.getMessage());
        }
    }

    private static void sendError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", message);

        resp.getWriter().write(gson.toJson(errorResponse));
    }
}