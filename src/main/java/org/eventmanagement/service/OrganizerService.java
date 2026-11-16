
package org.eventmanagement.service;

import org.eventmanagement.dto.OrganizerUpdateDTO;
import org.eventmanagement.entity.Organizer;
import org.eventmanagement.repository.OrganizerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrganizerService {

    private final OrganizerRepository organizerRepository;
    private final PasswordEncoder passwordEncoder;

    public OrganizerService(
            OrganizerRepository organizerRepository,
            PasswordEncoder passwordEncoder) {
        this.organizerRepository = organizerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Register a new organizer with ACTIVE status.
    public Organizer saveOrganizer(Organizer organizer) {
        organizer.setOrganizerId(null);
        organizer.setOrganizerAccountStatus("ACTIVE");

        organizer.setOrganizerPassword(
                passwordEncoder.encode(organizer.getOrganizerPassword()));

        return organizerRepository.save(organizer);
    }

    // Find organizer by ID.
    public Optional<Organizer> findOrganizerById(Integer organizerId) {
        return organizerRepository.findById(organizerId);
    }

    // Find organizer by email.
    public Optional<Organizer> findOrganizerByEmail(String organizerEmail) {
        return organizerRepository.findByOrganizerEmailIgnoreCase(
                organizerEmail.trim()
        );
    }

    // Delete organizer by ID.
    public void deleteOrganizerById(Integer organizerId) {
        organizerRepository.deleteById(organizerId);
    }

    // Get all organizers.
    public List<Organizer> findAllOrganizers() {
        return organizerRepository.findAll();
    }

    // Check whether organizer exists.
    public boolean existsOrganizerById(Integer organizerId) {
        return organizerRepository.existsById(organizerId);
    }

    // Update profile fields without allowing account-status changes.
    @Transactional
    public Organizer updateOrganizer(
            Integer organizerId,
            OrganizerUpdateDTO dto) {

        Organizer existingOrganizer =
                organizerRepository.findById(organizerId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Organizer not found"));

        if (dto.getOrganizerName() != null) {
            if (dto.getOrganizerName().isBlank()) {
                throw new IllegalArgumentException(
                        "Organizer name cannot be blank");
            }

            existingOrganizer.setOrganizerName(
                    dto.getOrganizerName().trim());
        }

        if (dto.getOrganizerEmail() != null) {
            String email = dto.getOrganizerEmail().trim();

            if (email.isBlank()
                    || !email.matches(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                throw new IllegalArgumentException(
                        "Enter a valid email");
            }

            organizerRepository.findByOrganizerEmail(email)
                    .filter(other ->
                            !other.getOrganizerId().equals(organizerId))
                    .ifPresent(other -> {
                        throw new IllegalArgumentException(
                                "Organizer email already exists");
                    });

            existingOrganizer.setOrganizerEmail(email);
        }

        if (dto.getOrganizerPhoneNumber() != null) {
            String phone = dto.getOrganizerPhoneNumber().trim();

            if (!phone.matches("^[6-9][0-9]{9}$")) {
                throw new IllegalArgumentException(
                        "Enter a valid mobile number");
            }

            existingOrganizer.setOrganizerPhoneNumber(phone);
        }

        // Only Admin-specific methods below may change account status.

        if (dto.getOrganizerPassword() != null) {
            String password = dto.getOrganizerPassword();

            if (password.isBlank() || password.length() < 8) {
                throw new IllegalArgumentException(
                        "Password must contain at least 8 characters");
            }

            existingOrganizer.setOrganizerPassword(
                    passwordEncoder.encode(password));
        }

        return organizerRepository.save(existingOrganizer);
    }

    // Admin action: block an organizer.
    @Transactional
    public Organizer blockOrganizer(Integer organizerId) {
        Organizer organizer =
                organizerRepository.findById(organizerId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Organizer not found"));

        organizer.setOrganizerAccountStatus("BLOCKED");

        return organizerRepository.save(organizer);
    }

    // Admin action: unblock an organizer.
    @Transactional
    public Organizer approveOrganizer(Integer organizerId) {
        Organizer organizer =
                organizerRepository.findById(organizerId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Organizer not found"));

        organizer.setOrganizerAccountStatus("ACTIVE");

        return organizerRepository.save(organizer);
    }

    // Use only for a controlled migration of legacy plaintext passwords.
    @Transactional
    public void encodeExistingOrganizerPassword(Integer organizerId) {
        Organizer organizer =
                organizerRepository.findById(organizerId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Organizer not found"));

        String password = organizer.getOrganizerPassword();

        if (password != null
                && !password.startsWith("$2a$")
                && !password.startsWith("$2b$")
                && !password.startsWith("$2y$")) {

            organizer.setOrganizerPassword(
                    passwordEncoder.encode(password));

            organizerRepository.save(organizer);
        }
    }
}