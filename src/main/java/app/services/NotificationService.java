package app.services;

import app.dao.NotificationDAO;
import app.dao.UserDAO;
import app.db.Database;
import app.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationService {

    private static final NotificationDAO notificationDAO = new NotificationDAO();
    private static final UserDAO userDAO = new UserDAO();

    // 1) Notif à tous les employés actifs : nouvel événement créé
    public static void notifyNewEvent(int eventId, String sport, LocalDateTime eventDateTime) {
        String msg = "Nouveau événement : " + sport + " (le " + formatDate(eventDateTime.toLocalDate()) + ")";
        List<User> employees = userDAO.findActiveEmployees();

        for (User u : employees) {
            notificationDAO.insertIfNotExists(u.getId(), "NEW_EVENT", msg, eventId);
        }
    }

    // 2) Notif inscription confirmée (employé)
    public static void notifyRegistrationConfirmed(int userId, int eventId, String sport, LocalDateTime eventDateTime) {
        String msg = "Inscription confirmée : " + sport + " (le " + formatDate(eventDateTime.toLocalDate()) + ")";
        notificationDAO.insertIfNotExists(userId, "REGISTRATION_CONFIRMED", msg, eventId);
    }

    // 3) Rappel : événements à venir dans les prochaines X heures (ex: 24h)
    // ⚠️ Comme event_date est DATE (pas datetime), on fait une fenêtre en jours.
    public static void generateEventRemindersForUser(int userId, int hoursBefore) {

        // convertit hoursBefore en nombre de jours à surveiller (24h => 1 jour)
        int days = Math.max(1, (int) Math.ceil(hoursBefore / 24.0));

        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(days);

        List<EventRow> upcoming = findUpcomingRegisteredEvents(userId, today, limit);

        for (EventRow e : upcoming) {
            String msg = "Rappel : événement bientôt (" + e.sport + ") le " + formatDate(e.eventDate);
            notificationDAO.insertIfNotExists(userId, "EVENT_REMINDER", msg, e.eventId);
        }
    }

    // ---- SQL: événements inscrits (REGISTERED) dont la date est dans [today, limit]
    private static List<EventRow> findUpcomingRegisteredEvents(int userId, LocalDate from, LocalDate to) {

        String sql = """
                SELECT e.id, e.sport, e.event_date
                FROM registrations r
                JOIN events e ON e.id = r.event_id
                WHERE r.employee_id = ?
                  AND r.status = 'REGISTERED'
                  AND e.status IN ('OPEN','PLANNED')
                  AND e.event_date >= ?
                  AND e.event_date <= ?
                ORDER BY e.event_date ASC
                """;

        List<EventRow> list = new ArrayList<>();

        try (Connection cnx = Database.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(to));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new EventRow(
                            rs.getInt("id"),
                            rs.getString("sport"),
                            rs.getDate("event_date").toLocalDate()
                    ));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findUpcomingRegisteredEvents", e);
        }

        return list;
    }

    private static String formatDate(LocalDate d) {
        return d.toString(); // simple: 2026-02-20
    }

    private static class EventRow {
        final int eventId;
        final String sport;
        final LocalDate eventDate;

        EventRow(int eventId, String sport, LocalDate eventDate) {
            this.eventId = eventId;
            this.sport = sport;
            this.eventDate = eventDate;
        }
    }
}
