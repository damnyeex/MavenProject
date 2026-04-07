package dao;

import models.Tenant;
import models.User;
import utils.DbHelper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class TenantRepository {

    private static Tenant mapTenant(ResultSet rs) throws SQLException {
        Tenant t = new Tenant(rs.getString("name"), rs.getString("description"));
        t.setId(rs.getObject("id").toString());
        return t;
    }

    public static List<Tenant> findAll() throws SQLException {
        return DbHelper.query("SELECT id, name, description FROM tenants", TenantRepository::mapTenant);
    }

    public static Tenant findById(String id) throws SQLException {
        String sql = "SELECT id, name, description FROM tenants WHERE id = ?";
        return DbHelper.queryOne(sql, TenantRepository::mapTenant, UUID.fromString(id));
    }

    public static void create(Tenant tenant) throws SQLException {
        String sql = "INSERT INTO tenants (id, name, description) VALUES (?, ?, ?)";
        try (var stmt = DbHelper.prepareStatement(sql,
                UUID.fromString(tenant.getId()),
                tenant.getName(),
                tenant.getDescription()
        )) {
            stmt.executeUpdate();
        }
    }

    public static void update(Tenant tenant) throws SQLException {
        String sql = "UPDATE tenants SET name = ?, description = ? WHERE id = ?";
        try (var stmt = DbHelper.prepareStatement(sql,
                tenant.getName(),
                tenant.getDescription(),
                UUID.fromString(tenant.getId())
        )) {
            stmt.executeUpdate();
        }
    }

    public static void delete(String id) throws SQLException {
        String sql = "DELETE FROM tenants WHERE id = ?";
        try (var stmt = DbHelper.prepareStatement(sql, UUID.fromString(id))) {
            stmt.executeUpdate();
        }
    }

    public static int getUserCount(String tenantId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE tenant_id = ?";
        return DbHelper.queryOne(sql, rs -> rs.getInt(1), UUID.fromString(tenantId));
    }

    public static List<User> getUsersByTenantId(String tenantId) throws SQLException {
        return UserRepository.findAllByTenantId(tenantId);
    }
}