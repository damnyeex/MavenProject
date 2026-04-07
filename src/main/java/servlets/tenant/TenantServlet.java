package servlets.tenant;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servlets.tenant.handlers.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/tenants/*")
public class TenantServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("=== TenantServlet doGet START ===");

        Object userIdObj = req.getAttribute("userId");
        String userIdAttr = userIdObj != null ? userIdObj.toString() : null;


        if (userIdAttr == null) {
            sendError(resp, 401, "Unauthorized");
            return;
        }

        String pathInfo = req.getPathInfo();

        // Если нет pathInfo или это "/" - возвращается список всех тенантов
        if (pathInfo == null || "/".equals(pathInfo)) {
            GetTenantsHandler.handle(req, resp);
            return;
        }

        // Иначе - возвращается конкретный тенант по ID с деталями
        GetTenantByIdHandler.handle(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("=== TenantServlet doPost START ===");

        Object userIdObj = req.getAttribute("userId");
        String userIdAttr = userIdObj != null ? userIdObj.toString() : null;
        String userRole = (String) req.getAttribute("role");

        if (userIdAttr == null) {
            sendError(resp, 401, "Unauthorized");
            return;
        }

        // Только ADMIN может создавать тенанты
        if (!"ADMIN".equals(userRole)) {
            sendError(resp, 403, "Access denied: Only administrators can create tenants");
            return;
        }

        CreateTenantHandler.handle(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("=== TenantServlet doPut START ===");

        Object userIdObj = req.getAttribute("userId");
        String userIdAttr = userIdObj != null ? userIdObj.toString() : null;
        String userRole = (String) req.getAttribute("role");

        if (userIdAttr == null) {
            sendError(resp, 401, "Unauthorized");
            return;
        }

        // Только ADMIN может обновлять тенанты
        if (!"ADMIN".equals(userRole)) {
            sendError(resp, 403, "Access denied: Only administrators can update tenants");
            return;
        }

        UpdateTenantHandler.handle(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("=== TenantServlet doDelete START ===");

        Object userIdObj = req.getAttribute("userId");
        String userIdAttr = userIdObj != null ? userIdObj.toString() : null;
        String userRole = (String) req.getAttribute("role");

        if (userIdAttr == null) {
            sendError(resp, 401, "Unauthorized");
            return;
        }

        // Только ADMIN может удалять тенанты
        if (!"ADMIN".equals(userRole)) {
            sendError(resp, 403, "Access denied: Only administrators can delete tenants");
            return;
        }

        DeleteTenantHandler.handle(req, resp);
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