package servlets;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@WebServlet("/hello")

public class HelloServlet extends HttpServlet {

    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Ответ будет в формате JSON
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // Простой объект для ответа
        Map<String, String> responseData = new HashMap<>();
        responseData.put("message", "Hello from Java Backend!");
        responseData.put("status", "success");

        // Перевод в JSON и отправка
        String jsonResponse = gson.toJson(responseData);
        resp.getWriter().write(jsonResponse);
    }
}