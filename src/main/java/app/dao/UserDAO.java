package app.dao;

import app.db.Database;
import app.model.Role;
import app.model.User;
import app.model.UserStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User findByEmail(String email) {
        String sql = "SELECT id, full_name, email, password_hash, role, status, created_at FROM users WHERE email = ?";

        try (Connection cnx = Database.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                return new User(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        Role.valueOf(rs.getString("role")),
                        UserStatus.valueOf(rs.getString("status")),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByEmail", e);
        }
    }

    public int insert(User user) {
        String sql = "INSERT INTO users(full_name, email, password_hash, role, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection cnx = Database.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole().name());
            ps.setString(5, user.getStatus().name());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
                return 0;
            }

        } catch (SQLIntegrityConstraintViolationException dup) {
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur insert user", e);
        }
    }

    public void updatePasswordHash(int userId, String newPasswordHash) {

        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";

        try (Connection cnx = Database.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setString(1, newPasswordHash);
            ps.setInt(2, userId);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur updatePasswordHash", e);
        }
    }

    // ✅ Liste des employés actifs (pour notifier lors d’un nouvel event)
    public List<User> findActiveEmployees() {

        String sql = "SELECT id, full_name, email, password_hash, role, status, created_at " +
                "FROM users WHERE role = 'EMPLOYEE' AND status = 'ACTIVE'";

        List<User> list = new ArrayList<>();

        try (Connection cnx = Database.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new User(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        Role.valueOf(rs.getString("role")),
                        UserStatus.valueOf(rs.getString("status")),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findActiveEmployees", e);
        }

        return list;
    }
}
