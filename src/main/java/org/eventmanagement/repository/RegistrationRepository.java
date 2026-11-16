
package org.eventmanagement.repository;

import org.eventmanagement.entity.Registration;
import org.eventmanagement.entity.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository
        extends JpaRepository<Registration, Integer> {

    boolean existsByUserUserIdAndEventEventIdAndRegistrationStatus(
            Integer userId,
            Integer eventId,
            RegistrationStatus registrationStatus);

    long countByEventEventIdAndRegistrationStatus(
            Integer eventId,
            RegistrationStatus registrationStatus);

    List<Registration> findByUserUserId(Integer userId);
    List<Registration> findByEventOrganizerOrganizerId(Integer organizerId);

    Optional<Registration>
    findByUserUserIdAndEventEventIdAndRegistrationStatus(
            Integer userId,
            Integer eventId,
            RegistrationStatus registrationStatus);
}