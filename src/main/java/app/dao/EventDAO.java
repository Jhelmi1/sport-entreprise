package app.dao;

import app.db.Database;
import app.model.Event;
import app.model.EventStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {

    // 🔹 CREATE EVENT (retourne l'ID généré)
    public int insert(Event event) {

        String sql = """
                INSERT INTO events
                (sport, event_date, location, capacity,
                 objective_id, created_by_rh, status, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, NOW())
                """;

        try (Connection cnx = Database.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, event.getSport());
            ps.setDate(2, Date.valueOf(event.getEventDate()));
            ps.setString(3, event.getLocation());
            ps.setInt(4, event.getCapacity());
            ps.setObject(5, event.getObjectiveId());
            ps.setInt(6, event.getCreatedByRh());
            ps.setString(7, event.getStatus().name());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

            return 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur insert Event", e);
        }
    }

    // 🔹 FIND ALL
    public List<Event> findAll() {

        String sql = """
                SELECT id, sport, event_date, location, capacity,
                       objective_id, created_by_rh, status, created_at
                FROM events
                ORDER BY event_date DESC
                """;

        List<Event> list = new ArrayList<>();

        try (Connection cnx = Database.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Event ev = new Event(
                        rs.getInt("id"),
                        rs.getString("sport"),
                        rs.getDate("event_date").toLocalDate(),
                        rs.getString("location"),
                        rs.getInt("capacity"),
                        (Integer) rs.getObject("objective_id"),
                        rs.getInt("created_by_rh"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );

                ev.setStatus(EventStatus.valueOf(rs.getString("status")));
                list.add(ev);
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll", e);
        }
    }

    // 🔹 FIND OPEN EVENTS (EMPLOYEE)
    public List<Event> findOpenEvents() {

        String sql = """
                SELECT id, sport, event_date, location, capacity,
                       objective_id, created_by_rh, status, created_at
                FROM events
                WHERE status = 'OPEN'
                ORDER BY event_date ASC
                """;

        List<Event> list = new ArrayList<>();

        try (Connection cnx = Database.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Event ev = new Event(
                        rs.getInt("id"),
                        rs.getString("sport"),
                        rs.getDate("event_date").toLocalDate(),
                        rs.getString("location"),
                        rs.getInt("capacity"),
                        (Integer) rs.getObject("objective_id"),
                        rs.getInt("created_by_rh"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );

                ev.setStatus(EventStatus.valueOf(rs.getString("status")));
                list.add(ev);
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findOpenEvents", e);
        }
    }

    // 🔹 FIND BY ID
    public Event findById(int id) {

        String sql = """
                SELECT id, sport, event_date, location, capacity,
                       objective_id, created_by_rh, status, created_at
                FROM events
                WHERE id = ?
                """;

        try (Connection cnx = Database.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Event ev = new Event(
                            rs.getInt("id"),
                            rs.getString("sport"),
                            rs.getDate("event_date").toLocalDate(),
                            rs.getString("location"),
                            rs.getInt("capacity"),
                            (Integer) rs.getObject("objective_id"),
                            rs.getInt("created_by_rh"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );

                    ev.setStatus(EventStatus.valueOf(rs.getString("status")));
                    return ev;
                }
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById", e);
        }
    }

    // 🔹 SOFT DELETE (CANCEL)
    public void cancelById(int id) {

        String sql = "UPDATE events SET status = 'CANCELLED' WHERE id = ?";

        try (Connection cnx = Database.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur cancelById", e);
        }
    }

    // 🔹 CHECK IF EVENT HAS REGISTRATIONS
    public boolean hasRegistrations(int eventId) {

        String sql = "SELECT COUNT(*) FROM registrations WHERE event_id = ?";

        try (Connection cnx = Database.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, eventId);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur hasRegistrations", e);
        }
    }

    // 🔹 AUTO CLOSE IF FULL
    public void closeIfFull(int eventId) {

        String sql = """
                UPDATE events
                SET status = 'CLOSED'
                WHERE id = ?
                  AND (
                      SELECT COUNT(*)
                      FROM registrations
                      WHERE event_id = ?
                        AND status = 'REGISTERED'
                  ) >= capacity
                """;

        try (Connection cnx = Database.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            ps.setInt(2, eventId);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur closeIfFull", e);
        }
    }
}
