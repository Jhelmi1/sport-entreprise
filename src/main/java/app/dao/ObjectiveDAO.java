package app.dao;

import app.db.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ObjectiveDAO {

    public void updateStatusAutomatically(int objectiveId) {

        String sql = """
            UPDATE objectives o
            SET status =
                CASE
                    WHEN (
                        SELECT COUNT(r.id)
                        FROM registrations r
                        JOIN events e ON e.id = r.event_id
                        WHERE e.objective_id = o.id
                          AND r.status = 'REGISTERED'
                    ) >= o.target_value
                    THEN 'ATTEINT'
                    ELSE 'NON_ATTEINT'
                END
            WHERE o.id = ?
            """;

        try (Connection cnx = Database.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, objectiveId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur updateStatusAutomatically", e);
        }
    }
}
