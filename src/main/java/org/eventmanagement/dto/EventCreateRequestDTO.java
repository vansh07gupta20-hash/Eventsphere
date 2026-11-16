package org.eventmanagement.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class EventCreateRequestDTO {

    @NotBlank(message = "Event title is required")
    @Size(max = 100, message = "Event title must not exceed 100 characters")
    private String eventTitle;

    private String eventDescription;

    @NotNull(message = "Event date and time is required")
    @Future(message = "Event date and time must be in the future")
    private LocalDateTime eventDateTime;

    @Size(max = 100, message = "Event location must not exceed 100 characters")
    private String eventLocation;

    @Positive(message = "Capacity must be greater than zero")
    private Integer usersCapacity;

    private LocalDateTime eventRegistrationStartDate;

    private LocalDateTime eventRegistrationEndDate;

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

    public void setEventRegistrationStartDate(LocalDateTime eventRegistrationStartDate) {
        this.eventRegistrationStartDate = eventRegistrationStartDate;
    }

    public LocalDateTime getEventRegistrationEndDate() {
        return eventRegistrationEndDate;
    }

    public void setEventRegistrationEndDate(LocalDateTime eventRegistrationEndDate) {
        this.eventRegistrationEndDate = eventRegistrationEndDate;
    }
}