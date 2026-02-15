package app.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Event {

    private int id;
    private String sport;
    private LocalDate eventDate;
    private String location;
    private int capacity;
    private Integer objectiveId;
    private int createdByRh;
    private EventStatus status;
    private LocalDateTime createdAt;

    public Event(int id, String sport, LocalDate eventDate, String location,
                 int capacity, Integer objectiveId, int createdByRh,
                 LocalDateTime createdAt) {

        this.id = id;
        this.sport = sport;
        this.eventDate = eventDate;
        this.location = location;
        this.capacity = capacity;
        this.objectiveId = objectiveId;
        this.createdByRh = createdByRh;
        this.createdAt = createdAt;
        this.status = EventStatus.PLANNED;
    }

    public Event(String sport, LocalDate eventDate, String location,
                 int capacity, Integer objectiveId, int createdByRh) {

        this.sport = sport;
        this.eventDate = eventDate;
        this.location = location;
        this.capacity = capacity;
        this.objectiveId = objectiveId;
        this.createdByRh = createdByRh;
        this.status = EventStatus.PLANNED;
    }

    public int getId() { return id; }
    public String getSport() { return sport; }
    public LocalDate getEventDate() { return eventDate; }
    public String getLocation() { return location; }
    public int getCapacity() { return capacity; }
    public Integer getObjectiveId() { return objectiveId; }
    public int getCreatedByRh() { return createdByRh; }
    public EventStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(int id) { this.id = id; }
    public void setStatus(EventStatus status) { this.status = status; }
}
