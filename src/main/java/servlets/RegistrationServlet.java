package servlets;

import com.google.gson.Gson;
import dao.UserRepository;
import models.User;
import utils.JwtUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@WebServlet("/api/auth/register")
public class RegistrationServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("=== RegistrationServlet doPost START ===");

        try {
            // 1. Тело запроса
            Map<String, String> requestBody = gson.fromJson(req.getReader(), Map.class);

            String login = requestBody.get("login");
            String password = requestBody.get("password");
            String role = requestBody.get("role");
            String tenantId = requestBody.get("tenantId");

            System.out.println("Registration attempt - login: " + login + ", role: " + role + ", tenantId: " + tenantId);

            // 2. Валидация обязательных полей
            if (login == null || login.trim().isEmpty()) {
                sendError(resp, 400, "Login is required");
                return;
            }

            if (password == null || password.trim().isEmpty()) {
                sendError(resp, 400, "Password is required");
                return;
            }

            // 3. Проверка на пользователя с таким login
            User existingUser = UserRepository.findByLogin(login);
            if (existingUser != null) {
                sendError(resp, 409, "User with this login already exists");
                return;
            }

            // 4. Устанавка роли по умолчанию (USER), если не указана
            String finalRole = (role == null || role.trim().isEmpty()) ? "USER" : role;

            // 5. Валидация роли (только ADMIN или USER)
            if (!"ADMIN".equals(finalRole) && !"USER".equals(finalRole)) {
                sendError(resp, 400, "Invalid role. Allowed values: ADMIN, USER");
                return;
            }

            // 6. Если роль USER, tenantId может быть null (пользователь без отдела)
            if ("USER".equals(finalRole) && tenantId != null && !tenantId.trim().isEmpty()) {
                System.out.println("User will be assigned to tenant: " + tenantId);
            }

            // 7. Создание нового пользователя
            User newUser = new User(login, password, finalRole, tenantId);
            newUser.setId(UUID.randomUUID().toString());

            UserRepository.create(newUser, password);
            System.out.println("User created successfully with ID: " + newUser.getId());

            // 8. Генерирация JWT токена
            String token = JwtUtil.generateToken(
                    UUID.fromString(newUser.getId()),
                    newUser.getLogin(),
                    newUser.getRole()
            );

            // 9. Установка cookie с токеном
            jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60);
            resp.addCookie(cookie);

            // 10. Отправка успешного ответа
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_CREATED); // 201 Created

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", newUser.getId());
            userMap.put("login", newUser.getLogin());
            userMap.put("role", newUser.getRole());
            userMap.put("tenantId", newUser.getTenantId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", token);
            response.put("user", userMap);

            resp.getWriter().write(gson.toJson(response));
            System.out.println("=== Registration completed successfully ===");

        } catch (SQLException e) {
            System.err.println("Database error during registration: " + e.getMessage());
            e.printStackTrace();
            sendError(resp, 500, "Database error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error during registration: " + e.getMessage());
            e.printStackTrace();
            sendError(resp, 500, "Internal server error");
        }
    }

    private void sendError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", message);
        resp.getWriter().write(gson.toJson(errorResponse));
    }
}