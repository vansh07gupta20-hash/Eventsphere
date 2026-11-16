package org.eventmanagement.service;

import org.eventmanagement.entity.Organizer;
import org.eventmanagement.repository.OrganizerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrganizerAuthService {

    private final OrganizerRepository organizerRepository;
    private final PasswordEncoder passwordEncoder;

    public OrganizerAuthService(
            OrganizerRepository organizerRepository,
            PasswordEncoder passwordEncoder) {
        this.organizerRepository = organizerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Organizer> login(String email, String password) {
        Optional<Organizer> organizerOptional =
                organizerRepository.findByOrganizerEmailIgnoreCase(email.trim());

        if (organizerOptional.isEmpty()) {
            return Optional.empty();
        }

        Organizer organizer = organizerOptional.get();

        if (!"ACTIVE".equals(organizer.getOrganizerAccountStatus())) {
            return Optional.empty();
        }

        if (!passwordEncoder.matches(password, organizer.getOrganizerPassword())) {
            return Optional.empty();
        }

        return Optional.of(organizer);
    }
}