package org.eventmanagement.controller;

import jakarta.validation.Valid;
import org.eventmanagement.dto.OrganizerLoginDTO;
import org.eventmanagement.dto.OrganizerLoginResponseDTO;
import org.eventmanagement.entity.Organizer;
import org.eventmanagement.service.JwtService;
import org.eventmanagement.service.OrganizerAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/organizer/auth")
public class OrganizerAuthController {

    private final OrganizerAuthService organizerAuthService;
    private final JwtService jwtService;

    public OrganizerAuthController(
            OrganizerAuthService organizerAuthService,
            JwtService jwtService) {
        this.organizerAuthService = organizerAuthService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody OrganizerLoginDTO dto) {

        Optional<Organizer> organizerOptional =
                organizerAuthService.login(
                        dto.getOrganizerEmail(),
                        dto.getOrganizerPassword()
                );

        if (organizerOptional.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        Organizer organizer = organizerOptional.get();
        String token = jwtService.generateOrganizerToken(organizer);

        return ResponseEntity.ok(new OrganizerLoginResponseDTO(
                organizer.getOrganizerId(),
                organizer.getOrganizerName(),
                organizer.getOrganizerEmail(),
                "Organizer login successful",
                token
        ));
    }
}