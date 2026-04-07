package servlets.user.handlers;

import com.google.gson.Gson;
import dao.UserRepository;
import models.User;
import utils.PasswordUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UpdateUserHandler {
    private static final Gson gson = new Gson();

    public static void handle(HttpServletRequest req, HttpServletResponse resp,
                              String userIdAttr, String userRole) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo)) {
            sendError(resp, 400, "User ID is required");
            return;
        }

        String idStr = pathInfo.substring(1);
        UUID targetUserId;

        try {
            targetUserId = UUID.fromString(idStr);
        } catch (IllegalArgumentException e) {
            sendError(resp, 400, "Invalid user ID format");
            return;
        }

        // Проверка прав: админ может обновлять любого, обычный пользователь - только себя
        if (!"ADMIN".equals(userRole) && !userIdAttr.equals(targetUserId.toString())) {
            sendError(resp, 403, "Access denied: You can only update your own profile");
            return;
        }

        try {
            // Чтение тела запроса
            Map<String, Object> requestBody = gson.fromJson(req.getReader(), Map.class);

            User existingUser = UserRepository.findById(targetUserId);
            if (existingUser == null) {
                sendError(resp, 404, "User not found");
                return;
            }

            // Обновление полей
            String newLogin = (String) requestBody.get("login");
            String newPassword = (String) requestBody.get("password");
            String newRole = (String) requestBody.get("role");
            String newTenantId = (String) requestBody.get("tenantId");

            // Обычный пользователь не может менять роль и tenantId
            if (!"ADMIN".equals(userRole)) {
                newRole = existingUser.getRole();
                newTenantId = existingUser.getTenantId();
            }

            if (newTenantId != null) {
                existingUser.setTenantId(newTenantId.isEmpty() ? null : newTenantId);
            }

            if (newLogin != null && !newLogin.trim().isEmpty()) {
                existingUser.setLogin(newLogin);
            }

            if (newPassword != null && !newPassword.trim().isEmpty()) {
                existingUser.setPassword(PasswordUtil.hash(newPassword));
            }

            if (newRole != null && !newRole.trim().isEmpty()) {
                if (!"ADMIN".equals(newRole) && !"USER".equals(newRole)) {
                    sendError(resp, 400, "Invalid role. Allowed values: ADMIN, USER");
                    return;
                }
                existingUser.setRole(newRole);
            }

            if (newTenantId != null) {
                existingUser.setTenantId(newTenantId.isEmpty() ? null : newTenantId);
            }

            UserRepository.update(existingUser);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User updated successfully");
            response.put("user", existingUser);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(gson.toJson(response));
            System.out.println("User updated successfully: " + targetUserId);

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
            sendError(resp, 500, "Database error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            sendError(resp, 500, "Internal server error");
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
        System.out.println("Error response: " + statusCode + " - " + message);
    }
}