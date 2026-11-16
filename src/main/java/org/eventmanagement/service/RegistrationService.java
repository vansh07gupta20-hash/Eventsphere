
package org.eventmanagement.service;

import org.eventmanagement.entity.Event;
import org.eventmanagement.entity.EventStatus;
import org.eventmanagement.entity.Registration;
import org.eventmanagement.entity.RegistrationStatus;
import org.eventmanagement.entity.User;
import org.eventmanagement.repository.EventRepository;
import org.eventmanagement.repository.RegistrationRepository;
import org.eventmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public RegistrationService(
            RegistrationRepository registrationRepository,
            EventRepository eventRepository,
            UserRepository userRepository) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Registration registerForEvent(Integer userId, Integer eventId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        if (!"ACTIVE".equals(user.getUserAccountStatus())) {
            throw new IllegalStateException(
                    "Inactive users cannot register for events");
        }

        Event event = eventRepository.findByIdForUpdate(eventId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Event not found"));

        if (event.getEventStatus() != EventStatus.SCHEDULED) {
            throw new IllegalStateException(
                    "Registration is allowed only for scheduled events");
        }

        LocalDateTime now = LocalDateTime.now();

        if (event.getEventRegistrationStartDate() != null
                && now.isBefore(event.getEventRegistrationStartDate())) {
            throw new IllegalStateException(
                    "Event registration has not started");
        }

        if (event.getEventRegistrationEndDate() != null
                && now.isAfter(event.getEventRegistrationEndDate())) {
            throw new IllegalStateException(
                    "Event registration has closed");
        }

        boolean alreadyRegistered =
                registrationRepository
                        .existsByUserUserIdAndEventEventIdAndRegistrationStatus(
                                userId,
                                eventId,
                                RegistrationStatus.REGISTERED);

        if (alreadyRegistered) {
            throw new IllegalStateException(
                    "User is already registered for this event");
        }

        if (event.getUsersCapacity() != null) {
            long registeredCount =
                    registrationRepository
                            .countByEventEventIdAndRegistrationStatus(
                                    eventId,
                                    RegistrationStatus.REGISTERED);

            if (registeredCount >= event.getUsersCapacity()) {
                throw new IllegalStateException(
                        "Event has reached its maximum capacity");
            }
        }

        Registration registration =
                registrationRepository
                        .findByUserUserIdAndEventEventIdAndRegistrationStatus(
                                userId,
                                eventId,
                                RegistrationStatus.CANCELLED)
                        .orElseGet(Registration::new);

        registration.setUser(user);
        registration.setEvent(event);
        registration.setRegistrationStatus(
                RegistrationStatus.REGISTERED);

        return registrationRepository.save(registration);
    }

    @Transactional(readOnly = true)
    public List<Registration> getUserRegistrations(Integer userId) {

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found");
        }

        return registrationRepository.findByUserUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Registration> getOrganizerRegistrations(Integer organizerId) {
        return registrationRepository
                .findByEventOrganizerOrganizerId(organizerId);
    }

    @Transactional
    public Registration cancelRegistration(
            Integer userId, Integer registrationId) {

        Registration registration = registrationRepository
                .findById(registrationId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Registration not found"));

        if (!registration.getUser().getUserId().equals(userId)) {
            throw new SecurityException(
                    "You can cancel only your own registrations");
        }

        if (registration.getRegistrationStatus()
                == RegistrationStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Registration is already cancelled");
        }

        registration.setRegistrationStatus(
                RegistrationStatus.CANCELLED);

        return registrationRepository.save(registration);
    }
}