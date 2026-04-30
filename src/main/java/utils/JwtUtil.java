package utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

public class JwtUtil {
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
            EnvLoader.get("JWT_SECRET").getBytes(StandardCharsets.UTF_8)
    );
    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000; // 24h

    public static String generateToken(UUID userId, String login, String role, String lastname, String firstname, String middlename) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("login", login)
                .claim("role", role)
                .claim("lastname", lastname)
                .claim("firstname", firstname)
                .claim("middlename", middlename)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static Claims verifyToken(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}