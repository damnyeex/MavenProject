package servlets;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.JwtUtil;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@WebServlet("/api/auth/check")
public class CheckServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UUID userId = (UUID) req.getAttribute("userId");
        String login = (String) req.getAttribute("login");
        String role = (String) req.getAttribute("role");
        String newToken = JwtUtil.generateToken(userId, login, role);
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(Map.of(
                "token", newToken,
                "user", Map.of("id", userId, "login", login, "role", role)
        )));
    }
}