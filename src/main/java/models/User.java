package models;

import java.util.UUID;

public class User {
    private String id;
    private String login;
    private String password; // в реальном проекте хешировать
    private String role;     // "ADMIN" или "USER"
    private String tenantId; // для USER — ID тенанта; для ADMIN — null

    public User(String login, String password, String role, String tenantId) {
        this.id = UUID.randomUUID().toString();
        this.login = login;
        this.password = password;
        this.role = role;
        this.tenantId = tenantId;
    }

    // Геттеры и сеттеры
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
}