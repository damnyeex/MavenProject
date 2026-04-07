package filters;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.JwtUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        System.out.println(">>> AuthFilter: " + req.getMethod() + " " + req.getRequestURI());

        // Пропускать OPTIONS и статику
        String path = req.getRequestURI();
        if ("OPTIONS".equals(req.getMethod()) || path.startsWith("/index.html") || path.equals("/")) {
            chain.doFilter(request, response);
            return;
        }

        // Пропускать логин (регистрацию)
        if (path.equals("/api/auth/login") ||
                path.equals("/api/auth/logout") ||
                path.equals("/api/auth/register")) {
            chain.doFilter(request, response);
            return;
        }

        Cookie[] cookies = req.getCookies();
        String token = null;
        if (cookies != null) {
            token = Arrays.stream(cookies)
                    .filter(c -> "token".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        if (token == null) {
            String authHeader = req.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
        }

        if (token == null) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing token");
            return;
        }

        try {
            Claims claims = JwtUtil.verifyToken(token);
            req.setAttribute("userId", UUID.fromString(claims.getSubject()));
            req.setAttribute("login", claims.get("login", String.class));
            req.setAttribute("role", claims.get("role", String.class));
            chain.doFilter(request, response);
        } catch (Exception e) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
        }
    }
}