package servlets.tenant.handlers;

import com.google.gson.Gson;
import dao.TenantRepository;
import models.Tenant;
import models.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetTenantByIdHandler {
    private static final Gson gson = new Gson();

    public static void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo)) {
            sendError(resp, 400, "Tenant ID is required");
            return;
        }

        String tenantId = pathInfo.substring(1);

        try {
            Tenant tenant = TenantRepository.findById(tenantId);
            if (tenant == null) {
                sendError(resp, 404, "Tenant not found");
                return;
            }

            // Получение списка пользователей по тенанту
            List<User> users = TenantRepository.getUsersByTenantId(tenantId);

            // Формирование детального ответ
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("id", tenant.getId());
            responseData.put("name", tenant.getName());
            responseData.put("description", tenant.getDescription());
            responseData.put("userCount", users.size());
            responseData.put("users", users);

            sendSuccess(resp, responseData);

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