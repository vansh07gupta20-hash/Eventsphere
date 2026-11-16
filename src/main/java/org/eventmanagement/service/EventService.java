package org.eventmanagement.service;

import org.eventmanagement.entity.Event;
import org.eventmanagement.entity.EventStatus;
import org.eventmanagement.entity.Organizer;
import org.eventmanagement.repository.EventRepository;
import org.eventmanagement.repository.OrganizerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final OrganizerRepository organizerRepository;

    public EventService(
            EventRepository eventRepository,
            OrganizerRepository organizerRepository) {
        this.eventRepository = eventRepository;
        this.organizerRepository = organizerRepository;
    }
    public Event saveEvent(Event event, Integer organizerId) {
        Organizer organizer = organizerRepository.findById(organizerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Organizer not found"));

        if (event.getEventRegistrationStartDate() != null
                && event.getEventRegistrationEndDate() != null
                && !event.getEventRegistrationEndDate()
                .isAfter(event.getEventRegistrationStartDate())) {
            throw new IllegalArgumentException(
                    "Registration end date must be after start date");
        }

        event.setOrganizer(organizer);
        event.setEventStatus(EventStatus.SCHEDULED);
        return eventRepository.save(event);
    }
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Integer eventId) {
        return eventRepository.findById(eventId);
    }
    public Optional<Event> updateEvent(
            Integer eventId,
            Integer organizerId,
            Event updatedEvent) {

        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if (eventOptional.isEmpty()) {
            return Optional.empty();
        }

        Event existingEvent = eventOptional.get();

        if (!existingEvent.getOrganizer().getOrganizerId()
                .equals(organizerId)) {
            throw new SecurityException(
                    "You can update only your own events");
        }

        if (updatedEvent.getEventRegistrationStartDate() != null
                && updatedEvent.getEventRegistrationEndDate() != null
                && !updatedEvent.getEventRegistrationEndDate()
                .isAfter(updatedEvent.getEventRegistrationStartDate())) {
            throw new IllegalArgumentException(
                    "Registration end date must be after start date");
        }

        existingEvent.setEventTitle(updatedEvent.getEventTitle());
        existingEvent.setEventDescription(updatedEvent.getEventDescription());
        existingEvent.setEventDateTime(updatedEvent.getEventDateTime());
        existingEvent.setEventLocation(updatedEvent.getEventLocation());
        existingEvent.setUsersCapacity(updatedEvent.getUsersCapacity());
        existingEvent.setEventRegistrationStartDate(
                updatedEvent.getEventRegistrationStartDate());
        existingEvent.setEventRegistrationEndDate(
                updatedEvent.getEventRegistrationEndDate());

        return Optional.of(eventRepository.save(existingEvent));
    }
    public boolean deleteEvent(Integer eventId, Integer organizerId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if (eventOptional.isEmpty()) {
            return false;
        }

        Event event = eventOptional.get();

        if (!event.getOrganizer().getOrganizerId().equals(organizerId)) {
            throw new SecurityException(
                    "You can delete only your own events");
        }

        eventRepository.delete(event);
        return true;
    }
    public List<Event> getOrganizerEvents(Integer organizerId) {
        return eventRepository.findByOrganizerOrganizerId(organizerId);
    }
}