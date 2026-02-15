package app.dao;

import app.db.Database;

import java.sql.*;

public class RegistrationDAO {

    public boolean exists(int eventId, int employeeId) {
        String sql = "SELECT COUNT(*) FROM registrations WHERE event_id = ? AND employee_id = ?";

        try (Connection cnx = Database.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            ps.setInt(2, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur exists registration", e);
        }
    }

    public void insert(int eventId, int employeeId) {
        String sql = "INSERT INTO registrations(event_id, employee_id, status) VALUES (?, ?, 'REGISTERED')";

        try (Connection cnx = Database.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            ps.setInt(2, employeeId);

            ps.executeUpdate();

        } catch (SQLIntegrityConstraintViolationException dup) {
            // cas double inscription (unique key)
            throw new RuntimeException("Déjà inscrit à cet événement.");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur insert registration", e);
        }
    }

    public int countForEvent(int eventId) {
        String sql = "SELECT COUNT(*) FROM registrations WHERE event_id = ? AND status = 'REGISTERED'";

        try (Connection cnx = Database.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, eventId);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur countForEvent", e);
        }
    }
    public java.util.List<app.model.Event> findEventsByEmployee(int employeeId) {

        String sql = """
        SELECT e.id, e.sport, e.event_date, e.location, e.capacity,
               e.objective_id, e.created_by_rh, e.status, e.created_at
        FROM events e
        JOIN registrations r ON e.id = r.event_id
        WHERE r.employee_id = ?
        ORDER BY e.event_date ASC
        """;

        java.util.List<app.model.Event> list = new java.util.ArrayList<>();

        try (Connection cnx = app.db.Database.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    app.model.Event ev = new app.model.Event(
                            rs.getInt("id"),
                            rs.getString("sport"),
                            rs.getDate("event_date").toLocalDate(),
                            rs.getString("location"),
                            rs.getInt("capacity"),
                            (Integer) rs.getObject("objective_id"),
                            rs.getInt("created_by_rh"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                    ev.setStatus(app.model.EventStatus.valueOf(rs.getString("status")));
                    list.add(ev);
                }
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findEventsByEmployee", e);
        }
    }
    public java.util.List<app.model.MyRegistrationRow> findRowsByEmployee(int employeeId) {

        String sql = """
        SELECT e.id AS event_id, e.sport, e.event_date, e.location,
               r.status AS reg_status
        FROM events e
        JOIN registrations r ON e.id = r.event_id
        WHERE r.employee_id = ?
        ORDER BY e.event_date ASC
        """;

        java.util.List<app.model.MyRegistrationRow> list = new java.util.ArrayList<>();

        try (java.sql.Connection cnx = app.db.Database.getConnection();
             java.sql.PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, employeeId);

            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new app.model.MyRegistrationRow(
                            rs.getInt("event_id"),
                            rs.getString("sport"),
                            rs.getDate("event_date").toLocalDate(),
                            rs.getString("location"),
                            rs.getString("reg_status")
                    ));
                }
            }

            return list;

        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Erreur findRowsByEmployee", e);
        }
    }

}
