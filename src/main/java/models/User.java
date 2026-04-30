package models;

public class User {
    private String id;
    private String login;
    private String password;
    private String role;
    private String firstname;
    private String lastname;
    private String middlename;
    private String tenantId;

    public User(String login, String password, String role, String tenantId, String lastname, String firstname, String middlename) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.tenantId = tenantId;
        this.firstname = lastname;
        this.lastname = firstname;
        this.middlename = middlename;
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
    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public String getMiddlename() { return middlename; }
    public void setMiddlename(String middlename) { this.middlename = middlename; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
}