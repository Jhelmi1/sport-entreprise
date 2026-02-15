package app.model;

import java.time.LocalDateTime;

public class Registration {

    public enum Status {
        REGISTERED,
        CANCELLED,
        ATTENDED,
        NO_SHOW
    }

    private int id;
    private int eventId;
    private int employeeId;
    private Status status;
    private LocalDateTime registeredAt;

    public Registration(int id, int eventId, int employeeId,
                        Status status, LocalDateTime registeredAt) {
        this.id = id;
        this.eventId = eventId;
        this.employeeId = employeeId;
        this.status = status;
        this.registeredAt = registeredAt;
    }

    public Registration(int eventId, int employeeId) {
        this.eventId = eventId;
        this.employeeId = employeeId;
        this.status = Status.REGISTERED;
    }

    public int getId() { return id; }
    public int getEventId() { return eventId; }
    public int getEmployeeId() { return employeeId; }
    public Status getStatus() { return status; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }

    public void setId(int id) { this.id = id; }
}
