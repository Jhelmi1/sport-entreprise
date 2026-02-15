package app.model;

import java.time.LocalDateTime;

public class Notification {

    private int id;
    private int userId;
    private String type;
    private String message;
    private int relatedId;   // NOT NULL dans ta table
    private boolean isRead;
    private LocalDateTime createdAt;

    // Constructeur complet (lecture depuis DB)
    public Notification(int id,
                        int userId,
                        String type,
                        String message,
                        int relatedId,
                        boolean isRead,
                        LocalDateTime createdAt) {

        this.id = id;
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.relatedId = relatedId;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // Constructeur insertion
    public Notification(int userId,
                        String type,
                        String message,
                        int relatedId) {

        this.userId = userId;
        this.type = type;
        this.message = message;
        this.relatedId = relatedId;
    }

    // Getters

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public int getRelatedId() {
        return relatedId;
    }

    public boolean isRead() {
        return isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setter utile si besoin futur

    public void setRead(boolean read) {
        this.isRead = read;
    }
}
