package org.eventmanagement.controller;

import jakarta.validation.Valid;
import org.eventmanagement.dto.EventCreateRequestDTO;
import org.eventmanagement.dto.EventResponse;
import org.eventmanagement.dto.EventUpdateRequestDTO;
import org.eventmanagement.entity.Event;
import org.eventmanagement.entity.Organizer;
import org.eventmanagement.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        List<EventResponse> events = eventService.getAllEvents()
                .stream()
                .map(EventResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(events);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEventById(
            @PathVariable Integer eventId) {

        return eventService.getEventById(eventId)
                .map(EventResponse::new)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<?> createEvent(
            Authentication authentication,
            @Valid @RequestBody EventCreateRequestDTO request) {

        if (authentication == null
                || !(authentication.getPrincipal() instanceof Organizer organizer)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only organizers can create events");
        }

        try {
            Event event = new Event();
            event.setEventTitle(request.getEventTitle());
            event.setEventDescription(request.getEventDescription());
            event.setEventDateTime(request.getEventDateTime());
            event.setEventLocation(request.getEventLocation());
            event.setUsersCapacity(request.getUsersCapacity());
            event.setEventRegistrationStartDate(
                    request.getEventRegistrationStartDate());
            event.setEventRegistrationEndDate(
                    request.getEventRegistrationEndDate());

            Event savedEvent = eventService.saveEvent(
                    event, organizer.getOrganizerId());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new EventResponse(savedEvent));

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    @PutMapping("/{eventId}")
    public ResponseEntity<?> updateEvent(
            @PathVariable Integer eventId,
            Authentication authentication,
            @Valid @RequestBody EventUpdateRequestDTO request) {

        if (authentication == null
                || !(authentication.getPrincipal() instanceof Organizer organizer)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only organizers can update events");
        }

        Event updatedEvent = new Event();
        updatedEvent.setEventTitle(request.getEventTitle());
        updatedEvent.setEventDescription(request.getEventDescription());
        updatedEvent.setEventDateTime(request.getEventDateTime());
        updatedEvent.setEventLocation(request.getEventLocation());
        updatedEvent.setUsersCapacity(request.getUsersCapacity());
        updatedEvent.setEventRegistrationStartDate(
                request.getEventRegistrationStartDate());
        updatedEvent.setEventRegistrationEndDate(
                request.getEventRegistrationEndDate());

        try {
            return eventService.updateEvent(
                            eventId,
                            organizer.getOrganizerId(),
                            updatedEvent)
                    .map(EventResponse::new)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());

        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ex.getMessage());

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(ex.getMessage());
        }
    }
    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(
            @PathVariable Integer eventId,
            Authentication authentication) {

        if (authentication == null
                || !(authentication.getPrincipal() instanceof Organizer organizer)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only organizers can delete events");
        }

        try {
            boolean deleted = eventService.deleteEvent(
                    eventId, organizer.getOrganizerId());

            if (!deleted) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.noContent().build();

        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ex.getMessage());
        }
    }
    @GetMapping("/organizer")
    public ResponseEntity<?> getOrganizerEvents(
            Authentication authentication) {

        if (authentication == null
                || !(authentication.getPrincipal() instanceof Organizer organizer)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only organizers can view their events");
        }

        List<EventResponse> events = eventService
                .getOrganizerEvents(organizer.getOrganizerId())
                .stream()
                .map(EventResponse::new)
                .toList();

        return ResponseEntity.ok(events);
    }
}