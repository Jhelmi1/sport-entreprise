package app.model;

import java.time.LocalDate;

public class MyRegistrationRow {

    private final int eventId;
    private final String sport;
    private final LocalDate eventDate;
    private final String location;
    private final String registrationStatus; // REGISTERED / CANCELLED

    public MyRegistrationRow(int eventId, String sport, LocalDate eventDate, String location, String registrationStatus) {
        this.eventId = eventId;
        this.sport = sport;
        this.eventDate = eventDate;
        this.location = location;
        this.registrationStatus = registrationStatus;
    }

    public int getEventId() { return eventId; }
    public String getSport() { return sport; }
    public LocalDate getEventDate() { return eventDate; }
    public String getLocation() { return location; }
    public String getRegistrationStatus() { return registrationStatus; }
}
