package app.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Objective {

    private int id;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private int targetValue;

    private ObjectiveStatus status;
    private ObjectiveResult result;

    private int createdByRh;
    private LocalDateTime createdAt;

    public Objective(int id, String title, String description,
                     LocalDate startDate, LocalDate endDate,
                     int targetValue,
                     ObjectiveStatus status,
                     ObjectiveResult result,
                     int createdByRh,
                     LocalDateTime createdAt) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.targetValue = targetValue;
        this.status = status;
        this.result = result;
        this.createdByRh = createdByRh;
        this.createdAt = createdAt;
    }

    public Objective(String title, String description,
                     LocalDate startDate, LocalDate endDate,
                     int targetValue,
                     int createdByRh) {

        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.targetValue = targetValue;
        this.createdByRh = createdByRh;

        this.status = ObjectiveStatus.DRAFT;
        this.result = ObjectiveResult.NOT_EVALUATED;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public int getTargetValue() { return targetValue; }
    public ObjectiveStatus getStatus() { return status; }
    public ObjectiveResult getResult() { return result; }
    public int getCreatedByRh() { return createdByRh; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(int id) { this.id = id; }
}
