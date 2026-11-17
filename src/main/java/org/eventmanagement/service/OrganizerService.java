
package org.eventmanagement.service;

import org.eventmanagement.dto.OrganizerUpdateDTO;
import org.eventmanagement.entity.Organizer;
import org.eventmanagement.entity.PasswordResetToken;
import org.eventmanagement.repository.OrganizerRepository;
import org.eventmanagement.repository.PasswordResetTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizerService {

    private final OrganizerRepository organizerRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    public OrganizerService(
            OrganizerRepository organizerRepository,
            PasswordEncoder passwordEncoder,
            PasswordResetTokenRepository passwordResetTokenRepository,
            EmailService emailService) {

        this.organizerRepository = organizerRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
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
    @Transactional
    public Optional<PasswordResetToken> generateOrganizerPasswordResetToken(
            String organizerEmail) {

        Optional<Organizer> organizerOptional =
                organizerRepository.findByOrganizerEmailIgnoreCase(
                        organizerEmail.trim()
                );

        if (organizerOptional.isEmpty()) {
            return Optional.empty();
        }

        Organizer organizer = organizerOptional.get();

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();

        // Link token to Organizer, not User.
        resetToken.setOrganizer(organizer);
        resetToken.setUser(null);

        resetToken.setResetToken(token);
        resetToken.setResetTokenExpiry(
                LocalDateTime.now().plusMinutes(30)
        );
        resetToken.setResetTokenUsed(false);

        PasswordResetToken savedToken =
                passwordResetTokenRepository.save(resetToken);

        emailService.sendPasswordResetEmail(
                organizer.getOrganizerEmail(),
                token
        );

        return Optional.of(savedToken);
    }
    @Transactional
    public Optional<Organizer> resetOrganizerPassword(
            String resetToken,
            String newPassword,
            String confirmPassword) {

        Optional<PasswordResetToken> tokenOptional =
                passwordResetTokenRepository.findByResetToken(resetToken);

        if (tokenOptional.isEmpty()) {
            return Optional.empty();
        }

        PasswordResetToken passwordResetToken = tokenOptional.get();

        // Verify token belongs to an Organizer.
        if (passwordResetToken.getOrganizer() == null
                || passwordResetToken.getUser() != null) {
            return Optional.empty();
        }

        // Check whether token has already been used.
        if (Boolean.TRUE.equals(passwordResetToken.getResetTokenUsed())) {
            return Optional.empty();
        }

        // Check token expiry.
        if (passwordResetToken.getResetTokenExpiry()
                .isBefore(LocalDateTime.now())) {
            return Optional.empty();
        }

        // Validate passwords.
        if (newPassword == null
                || confirmPassword == null
                || !newPassword.equals(confirmPassword)
                || newPassword.length() < 8
                || newPassword.length() > 72) {
            return Optional.empty();
        }

        Organizer organizer = passwordResetToken.getOrganizer();

        // Update encoded password.
        organizer.setOrganizerPassword(
                passwordEncoder.encode(newPassword)
        );

        Organizer savedOrganizer =
                organizerRepository.save(organizer);

        // Invalidate all reset tokens belonging to this Organizer.
        List<PasswordResetToken> tokens =
                passwordResetTokenRepository.findByOrganizer(organizer);

        for (PasswordResetToken token : tokens) {
            token.setResetTokenUsed(true);
        }

        passwordResetTokenRepository.saveAll(tokens);

        return Optional.of(savedOrganizer);
    }
}