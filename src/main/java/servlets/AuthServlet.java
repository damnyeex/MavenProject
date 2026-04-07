package servlets;

import com.google.gson.Gson;
import dao.UserRepository;
import models.User;
import utils.JwtUtil;
import utils.PasswordUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println(">>> AuthServlet.doPost called, pathInfo=" + req.getPathInfo());
        String path = req.getPathInfo();

        if ("/login".equals(path)) {
            Map<String, String> creds = gson.fromJson(req.getReader(), Map.class);
            String login = creds.get("login");
            String password = creds.get("password");

            try {
                String hashed = UserRepository.getPasswordHash(login);
                if (hashed == null || !PasswordUtil.verify(password, hashed)) {
                    resp.sendError(401, "Invalid credentials");
                    return;
                }
                User user = UserRepository.findByLogin(login);
                String token = JwtUtil.generateToken(UUID.fromString(user.getId()), user.getLogin(), user.getRole());

                jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("token", token);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(24 * 60 * 60);
                resp.addCookie(cookie);

                resp.setContentType("application/json");
                resp.getWriter().write(gson.toJson(Map.of("success", true, "role", user.getRole(), "token", token)));
            } catch (SQLException e) {
                resp.sendError(500, "DB error: " + e.getMessage());
            }
        } else if ("/logout".equals(path)) {
            jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("token", "");
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            resp.addCookie(cookie);
            resp.setStatus(200);
        } else {
            resp.sendError(404);
        }
    }
}