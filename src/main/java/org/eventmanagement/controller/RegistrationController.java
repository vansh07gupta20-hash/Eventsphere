
package org.eventmanagement.controller;

import org.eventmanagement.entity.Organizer;
import org.eventmanagement.entity.Registration;
import org.eventmanagement.entity.User;
import org.eventmanagement.service.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(
            RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    // Register for an event
    @PostMapping("/events/{eventId}")
    public ResponseEntity<?> registerForEvent(
            @PathVariable Integer eventId,
            Authentication authentication) {

        if (authentication == null
                || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message",
                            "Only logged-in users can register"));
        }

        try {
            Registration registration =
                    registrationService.registerForEvent(
                            user.getUserId(), eventId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(toResponse(registration));

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", ex.getMessage()));

        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    // View the logged-in user's registrations
    @GetMapping("/my")
    public ResponseEntity<?> getMyRegistrations(
            Authentication authentication) {

        if (authentication == null
                || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message",
                            "Only logged-in users can view registrations"));
        }

        try {
            List<Map<String, Object>> registrations =
                    registrationService
                            .getUserRegistrations(user.getUserId())
                            .stream()
                            .map(this::toResponse)
                            .toList();

            return ResponseEntity.ok(registrations);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    // Cancel your own registration
    @PatchMapping("/{registrationId}/cancel")
    public ResponseEntity<?> cancelRegistration(
            @PathVariable Integer registrationId,
            Authentication authentication) {

        if (authentication == null
                || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message",
                            "Only logged-in users can cancel registrations"));
        }

        try {
            Registration registration =
                    registrationService.cancelRegistration(
                            user.getUserId(), registrationId);

            return ResponseEntity.ok(toResponse(registration));

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", ex.getMessage()));

        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", ex.getMessage()));

        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    // Return a simple response instead of serializing JPA entities
    private Map<String, Object> toResponse(Registration registration) {
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("registrationId", registration.getRegistrationId());

        User user = registration.getUser();

        response.put("userId", user.getUserId());
        response.put("userName", user.getUserName());
        response.put("userEmail", user.getUserEmail());

        response.put("eventId", registration.getEvent().getEventId());
        response.put("eventTitle", registration.getEvent().getEventTitle());

        response.put("registrationDate", registration.getRegistrationDate());
        response.put(
                "registrationStatus",
                registration.getRegistrationStatus().name()
        );

        return response;
    }
    // View registrations for events created by the logged-in organizer
    @GetMapping("/organizer")
    public ResponseEntity<?> getOrganizerRegistrations(
            Authentication authentication) {

        if (authentication == null
                || !(authentication.getPrincipal() instanceof Organizer organizer)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message",
                            "Only logged-in organizers can view these registrations"));
        }

        List<Map<String, Object>> registrations =
                registrationService
                        .getOrganizerRegistrations(organizer.getOrganizerId())
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(registrations);
    }
}