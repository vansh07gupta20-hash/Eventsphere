
package org.eventmanagement.controller;

import jakarta.validation.Valid;
import org.eventmanagement.dto.OrganizerResponseDTO;
import org.eventmanagement.dto.OrganizerUpdateDTO;
import org.eventmanagement.entity.Organizer;
import org.eventmanagement.service.OrganizerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/organizers")
public class OrganizerController {

    private final OrganizerService organizerService;

    public OrganizerController(OrganizerService organizerService) {
        this.organizerService = organizerService;
    }

    // Get organizer by ID - Admin only through SecurityConfig.
    @GetMapping("/{organizerId}")
    public ResponseEntity<OrganizerResponseDTO> getOrganizerById(
            @PathVariable Integer organizerId) {

        return organizerService.findOrganizerById(organizerId)
                .map(OrganizerResponseDTO::new)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get all organizers - Admin only.
    @GetMapping
    public List<OrganizerResponseDTO> getAllOrganizer() {
        return organizerService.findAllOrganizers()
                .stream()
                .map(OrganizerResponseDTO::new)
                .collect(Collectors.toList());
    }

    // Find organizer by email - Admin only.
    @GetMapping("/email/{organizerEmail}")
    public ResponseEntity<OrganizerResponseDTO> getOrganizerByEmail(
            @PathVariable String organizerEmail) {

        return organizerService.findOrganizerByEmail(organizerEmail)
                .map(OrganizerResponseDTO::new)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Public registration.
    @PostMapping
    public ResponseEntity<?> createOrganizer(
            @Valid @RequestBody Organizer organizer) {

        String email = organizer.getOrganizerEmail().trim();

        if (organizerService.findOrganizerByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "message",
                            "Organizer email already exists"
                    ));
        }

        // Do not accept an ID or account status from the client.
        organizer.setOrganizerId(null);
        organizer.setOrganizerEmail(email);
        organizer.setOrganizerAccountStatus("ACTIVE");

        Organizer savedOrganizer =
                organizerService.saveOrganizer(organizer);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "organizerId", savedOrganizer.getOrganizerId(),
                        "organizerName", savedOrganizer.getOrganizerName(),
                        "organizerEmail", savedOrganizer.getOrganizerEmail(),
                        "message", "Organizer registered successfully"
                ));
    }

    // Update organizer - Admin only through SecurityConfig.
    @PutMapping("/{organizerId}")
    public ResponseEntity<?> updateOrganizer(
            @PathVariable Integer organizerId,
            @RequestBody OrganizerUpdateDTO dto) {

        try {
            Organizer updatedOrganizer =
                    organizerService.updateOrganizer(organizerId, dto);

            return ResponseEntity.ok(
                    new OrganizerResponseDTO(updatedOrganizer));

        } catch (IllegalArgumentException ex) {
            if ("Organizer not found".equals(ex.getMessage())) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.badRequest()
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    // Admin action: block an organizer.
    @PatchMapping("/{organizerId}/block")
    public ResponseEntity<?> blockOrganizer(
            @PathVariable Integer organizerId) {

        try {
            Organizer organizer =
                    organizerService.blockOrganizer(organizerId);

            return ResponseEntity.ok(Map.of(
                    "organizerId", organizer.getOrganizerId(),
                    "organizerAccountStatus",
                    organizer.getOrganizerAccountStatus(),
                    "message", "Organizer blocked successfully"
            ));

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound()
                    .build();
        }
    }

    // Admin action: unblock an organizer.
    @PatchMapping("/{organizerId}/unblock")
    public ResponseEntity<?> unblockOrganizer(
            @PathVariable Integer organizerId) {

        try {
            Organizer organizer =
                    organizerService.approveOrganizer(organizerId);

            return ResponseEntity.ok(Map.of(
                    "organizerId", organizer.getOrganizerId(),
                    "organizerAccountStatus",
                    organizer.getOrganizerAccountStatus(),
                    "message", "Organizer unblocked successfully"
            ));

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound()
                    .build();
        }
    }

    // Delete organizer - Admin only through SecurityConfig.
    @DeleteMapping("/{organizerId}")
    public ResponseEntity<Void> deleteOrganizer(
            @PathVariable Integer organizerId) {

        if (!organizerService.existsOrganizerById(organizerId)) {
            return ResponseEntity.notFound().build();
        }

        organizerService.deleteOrganizerById(organizerId);
        return ResponseEntity.noContent().build();
    }
}