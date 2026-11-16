package org.eventmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Event_ID")
    private Integer eventId;

    @NotBlank(message = "Event title is required")
    @Size(max = 100, message = "Event title must not exceed 100 characters")
    @Column(name = "Event_Title", nullable = false, length = 100)
    private String eventTitle;

    @Column(name = "Event_Description", columnDefinition = "TEXT")
    private String eventDescription;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Organizer_ID", nullable = false)
    private Organizer organizer;

    @NotNull(message = "Event date and time is required")
    @Future(message = "Event date and time must be in the future")
    @Column(name = "Event_Date_Time", nullable = false)
    private LocalDateTime eventDateTime;

    @Size(max = 100, message = "Event location must not exceed 100 characters")
    @Column(name = "Event_Location", length = 100)
    private String eventLocation;

    @Positive(message = "Capacity must be greater than zero")
    @Column(name = "Users_Capacity")
    private Integer usersCapacity;

    @Column(name = "Event_Registration_Start_Date")
    private LocalDateTime eventRegistrationStartDate;

    @Column(name = "Event_Registration_End_Date")
    private LocalDateTime eventRegistrationEndDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "Event_Status", nullable = false)
    private EventStatus eventStatus = EventStatus.SCHEDULED;

    public Event() {
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public Organizer getOrganizer() {
        return organizer;
    }

    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
    }

    public LocalDateTime getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(LocalDateTime eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public Integer getUsersCapacity() {
        return usersCapacity;
    }

    public void setUsersCapacity(Integer usersCapacity) {
        this.usersCapacity = usersCapacity;
    }

    public LocalDateTime getEventRegistrationStartDate() {
        return eventRegistrationStartDate;
    }

    public void setEventRegistrationStartDate(
            LocalDateTime eventRegistrationStartDate) {
        this.eventRegistrationStartDate = eventRegistrationStartDate;
    }

    public LocalDateTime getEventRegistrationEndDate() {
        return eventRegistrationEndDate;
    }

    public void setEventRegistrationEndDate(
            LocalDateTime eventRegistrationEndDate) {
        this.eventRegistrationEndDate = eventRegistrationEndDate;
    }

    public EventStatus getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }
}
