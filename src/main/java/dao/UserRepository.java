package dao;

import models.User;
import utils.DbHelper;
import utils.PasswordUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class UserRepository {

    private static User mapUser(ResultSet rs) throws SQLException {
        User u = new User(
                rs.getString("login"),
                null,
                rs.getString("role"),
                rs.getString("tenant_id"),
                rs.getString("lastname"),
                rs.getString("firstname"),
                rs.getString("middlename")
        );
        u.setId(rs.getObject("id").toString());
        return u;
    }

    public static User findByLogin(String login) throws SQLException {
        String sql = "SELECT id, login, role, firstname, lastname, middlename, tenant_id FROM users WHERE login = ?";
        return DbHelper.queryOne(sql, UserRepository::mapUser, login);
    }

    public static User findById(UUID id) throws SQLException {
        String sql = "SELECT id, login, role, firstname, lastname, middlename, tenant_id FROM users WHERE id = ?";
        return DbHelper.queryOne(sql, UserRepository::mapUser, id);
    }

    public static List<User> findAllByTenantId(String tenantId) throws SQLException {
        String sql = "SELECT id, login, role, firstname, lastname, middlename, tenant_id FROM users WHERE tenant_id = ?";
        return DbHelper.query(sql, UserRepository::mapUser, UUID.fromString(tenantId));
    }

    public static List<User> findAll() throws SQLException {
        String sql = "SELECT id, login, role, firstname, lastname, middlename, tenant_id FROM users";
        return DbHelper.query(sql, UserRepository::mapUser);
    }

    public static void update(User user) throws SQLException {
        String sql = "UPDATE users SET login = ?, role = ?, tenant_id = ?, firstname = ?, lastname = ?, middlename = ? WHERE id = ?";
        try (var stmt = DbHelper.prepareStatement(sql,
                user.getLogin(),
                user.getRole(),
                user.getTenantId() == null ? null : UUID.fromString(user.getTenantId()),
                user.getFirstname(),
                user.getLastname(),
                user.getMiddlename(),
                UUID.fromString(user.getId())
        )) {
            stmt.executeUpdate();
        }
    }

    public static void create(User user, String rawPassword) throws SQLException {
        String sql = "INSERT INTO users (id, login, password, role, firstname, lastname, middlename, tenant_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (var stmt = DbHelper.prepareStatement(sql,
                UUID.fromString(user.getId()),
                user.getLogin(),
                PasswordUtil.hash(rawPassword),
                user.getRole(),
                user.getFirstname(),
                user.getLastname(),
                user.getMiddlename(),
                user.getTenantId() == null ? null : UUID.fromString(user.getTenantId())
        )) {
            stmt.executeUpdate();
        }
    }

    public static String getPasswordHash(String login) throws SQLException {
        String sql = "SELECT password FROM users WHERE login = ?";
        return DbHelper.queryOne(sql, rs -> rs.getString("password"), login);
    }
}