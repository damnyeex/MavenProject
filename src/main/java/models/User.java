package models;

public class User {
    private String id;
    private String login;
    private String password;
    private String role;
    private String tenantId;

    public User(String login, String password, String role, String tenantId) {
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