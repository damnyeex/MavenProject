package servlets.user.handlers;

import com.google.gson.Gson;
import dao.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeleteUserHandler {
    private static final Gson gson = new Gson();

    public static void handle(HttpServletRequest req, HttpServletResponse resp,
                              String userIdAttr, String userRole) throws IOException {
        // Только ADMIN может удалять пользователей
        if (!"ADMIN".equals(userRole)) {
            sendError(resp, 403, "Access denied: Only administrators can delete users");
            return;
        }

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

        // Нельзя удалить самого себя
        if (userIdAttr.equals(targetUserId.toString())) {
            sendError(resp, 400, "You cannot delete your own account");
            return;
        }

        try {
            // Проверка, существует ли пользователь
            var user = UserRepository.findById(targetUserId);
            if (user == null) {
                sendError(resp, 404, "User not found");
                return;
            }

            boolean deleted = deleteUserFromDatabase(targetUserId);

            if (deleted) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "User deleted successfully");

                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().write(gson.toJson(response));
                System.out.println("User deleted successfully: " + targetUserId);
            } else {
                sendError(resp, 500, "Failed to delete user");
            }

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

    private static boolean deleteUserFromDatabase(UUID userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (var stmt = utils.DbHelper.prepareStatement(sql, userId)) {
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
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