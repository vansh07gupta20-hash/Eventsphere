package org.eventmanagement.repository;

import org.eventmanagement.entity.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizerRepository extends JpaRepository<Organizer, Integer> {

    Optional<Organizer> findByOrganizerEmail(String organizerEmail);

    Optional<Organizer> findByOrganizerEmailIgnoreCase(String organizerEmail);
}