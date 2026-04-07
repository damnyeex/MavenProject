package servlets.user;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import servlets.user.handlers.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("=== UserServlet doGet START ===");


        Object userIdObj = req.getAttribute("userId");
        String userIdAttr = userIdObj != null ? userIdObj.toString() : null;
        String userRole = (String) req.getAttribute("role");

        System.out.println("userIdAttr: " + userIdAttr);
        System.out.println("userRole: " + userRole);

        if (userIdAttr == null) {
            sendError(resp, 401, "Unauthorized");
            return;
        }

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || "/".equals(pathInfo)) {
            GetUsersHandler.handle(req, resp, userIdAttr, userRole);
            return;
        }

        GetUserByIdHandler.handle(req, resp, userIdAttr, userRole);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("=== UserServlet doPut START ===");

        Object userIdObj = req.getAttribute("userId");
        String userIdAttr = userIdObj != null ? userIdObj.toString() : null;
        String userRole = (String) req.getAttribute("role");

        if (userIdAttr == null) {
            sendError(resp, 401, "Unauthorized");
            return;
        }

        UpdateUserHandler.handle(req, resp, userIdAttr, userRole);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("=== UserServlet doDelete START ===");

        Object userIdObj = req.getAttribute("userId");
        String userIdAttr = userIdObj != null ? userIdObj.toString() : null;
        String userRole = (String) req.getAttribute("role");

        if (userIdAttr == null) {
            sendError(resp, 401, "Unauthorized");
            return;
        }

        DeleteUserHandler.handle(req, resp, userIdAttr, userRole);
    }

    private void sendError(HttpServletResponse resp, int statusCode, String message) throws IOException {
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