package servlets.user.handlers;

import com.google.gson.Gson;
import dao.UserRepository;
import models.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GetUserByIdHandler {
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

        try {
            User user = UserRepository.findById(targetUserId);

            if (user == null) {
                sendError(resp, 404, "User not found");
                return;
            }

            // Проверка прав: админ может видеть любого, обычный пользователь - только себя
            if (!"ADMIN".equals(userRole) && !userIdAttr.equals(user.getId())) {
                sendError(resp, 403, "Access denied: You can only view your own profile");
                return;
            }

            sendSuccess(resp, user);

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