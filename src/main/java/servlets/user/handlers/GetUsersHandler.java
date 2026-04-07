package servlets.user.handlers;

import com.google.gson.Gson;
import dao.UserRepository;
import models.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GetUsersHandler {
    private static final Gson gson = new Gson();

    public static void handle(HttpServletRequest req, HttpServletResponse resp,
                              String userIdAttr, String userRole) throws IOException {
        try {
            List<User> users;

            // Админ видит всех пользователей, обычный пользователь - только пользователей своего тенанта
            if ("ADMIN".equals(userRole)) {
                users = UserRepository.findAll();
                System.out.println("Admin requesting all users, found: " + users.size());
            } else {
                UUID currentUserId = UUID.fromString(userIdAttr);
                User currentUser = UserRepository.findById(currentUserId);
                String tenantId = currentUser != null ? currentUser.getTenantId() : null;

                if (tenantId != null) {
                    users = UserRepository.findAllByTenantId(tenantId);
                } else {
                    users = List.of();
                }
                System.out.println("Regular user requesting users from tenant: " + tenantId);
            }

            sendSuccess(resp, users);

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