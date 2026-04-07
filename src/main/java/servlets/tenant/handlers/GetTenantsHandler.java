package servlets.tenant.handlers;

import com.google.gson.Gson;
import dao.TenantRepository;
import models.Tenant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetTenantsHandler {
    private static final Gson gson = new Gson();

    public static void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<Tenant> tenants = TenantRepository.findAll();

            // Формирование ответа с количеством пользователей для каждого тенанта
            List<Map<String, Object>> responseList = new ArrayList<>();
            for (Tenant tenant : tenants) {
                Map<String, Object> tenantInfo = new HashMap<>();
                tenantInfo.put("id", tenant.getId());
                tenantInfo.put("name", tenant.getName());
                tenantInfo.put("description", tenant.getDescription());
                tenantInfo.put("userCount", TenantRepository.getUserCount(tenant.getId()));

                responseList.add(tenantInfo);
            }

            sendSuccess(resp, responseList);

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
            sendError(resp, 500, "Database error: " + e.getMessage());
        }
    }

    private static void sendSuccess(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);

        resp.getWriter().write(gson.toJson(response));
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