package app.dao;

import app.db.Database;
import app.model.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    // ✅ insert anti-duplication (UNIQUE user_id,type,related_id)
    public void insertIfNotExists(int userId, String type, String message, int relatedId) {

        String sql = "INSERT IGNORE INTO notifications(user_id, type, message, related_id) VALUES (?, ?, ?, ?)";

        try (Connection cnx = Database.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, type);
            ps.setString(3, message);
            ps.setInt(4, relatedId);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur insertIfNotExists", e);
        }
    }

    public List<Notification> findByUser(int userId) {

        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        List<Notification> list = new ArrayList<>();

        try (Connection cnx = Database.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByUser", e);
        }

        return list;
    }

    // ✅ utile pour refresh + son : seulement non lues
    public List<Notification> findUnreadByUser(int userId) {

        String sql = "SELECT * FROM notifications WHERE user_id = ? AND is_read = FALSE ORDER BY created_at DESC";
        List<Notification> list = new ArrayList<>();

        try (Connection cnx = Database.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findUnreadByUser", e);
        }

        return list;
    }

    public void markAsRead(int notificationId) {

        String sql = "UPDATE notifications SET is_read = TRUE WHERE id = ?";

        try (Connection cnx = Database.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, notificationId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur markAsRead", e);
        }
    }

    // ✅ 4) Marquer toutes comme lues
    public void markAllAsRead(int userId) {

        String sql = "UPDATE notifications SET is_read = TRUE WHERE user_id = ? AND is_read = FALSE";

        try (Connection cnx = Database.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur markAllAsRead", e);
        }
    }

    public int countUnread(int userId) {

        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = FALSE";

        try (Connection cnx = Database.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur countUnread", e);
        }

        return 0;
    }

    private Notification map(ResultSet rs) throws SQLException {
        return new Notification(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("type"),
                rs.getString("message"),
                rs.getInt("related_id"),
                rs.getBoolean("is_read"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
