package org.eventmanagement.dto;

import org.eventmanagement.entity.Event;
import java.time.LocalDateTime;

public class EventResponse {

    private Integer eventId;
    private String eventTitle;
    private String eventDescription;
    private Integer organizerId;
    private LocalDateTime eventDateTime;
    private String eventLocation;
    private Integer usersCapacity;
    private LocalDateTime eventRegistrationStartDate;
    private LocalDateTime eventRegistrationEndDate;
    private String eventStatus;

    public EventResponse(Event event) {
        this.eventId = event.getEventId();
        this.eventTitle = event.getEventTitle();
        this.eventDescription = event.getEventDescription();
        this.organizerId = event.getOrganizer().getOrganizerId();
        this.eventDateTime = event.getEventDateTime();
        this.eventLocation = event.getEventLocation();
        this.usersCapacity = event.getUsersCapacity();
        this.eventRegistrationStartDate = event.getEventRegistrationStartDate();
        this.eventRegistrationEndDate = event.getEventRegistrationEndDate();
        this.eventStatus = event.getEventStatus().name();
    }

    public Integer getEventId() { return eventId; }
    public String getEventTitle() { return eventTitle; }
    public String getEventDescription() { return eventDescription; }
    public Integer getOrganizerId() { return organizerId; }
    public LocalDateTime getEventDateTime() { return eventDateTime; }
    public String getEventLocation() { return eventLocation; }
    public Integer getUsersCapacity() { return usersCapacity; }
    public LocalDateTime getEventRegistrationStartDate() { return eventRegistrationStartDate; }
    public LocalDateTime getEventRegistrationEndDate() { return eventRegistrationEndDate; }
    public String getEventStatus() { return eventStatus; }
}
