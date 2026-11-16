package org.eventmanagement.repository;

import jakarta.persistence.LockModeType;
import org.eventmanagement.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {

    List<Event> findByOrganizerOrganizerId(Integer organizerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Event e WHERE e.eventId = :eventId")
    Optional<Event> findByIdForUpdate(@Param("eventId") Integer eventId);
}