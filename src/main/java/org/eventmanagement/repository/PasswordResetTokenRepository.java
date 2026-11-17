
package org.eventmanagement.repository;

import org.eventmanagement.entity.PasswordResetToken;
import org.eventmanagement.entity.User;
import org.eventmanagement.entity.Organizer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Integer> {

    Optional<PasswordResetToken> findByResetToken(String resetToken);

    // Find all reset tokens belonging to a User.
    List<PasswordResetToken> findByUser(User user);

    // Find all reset tokens belonging to an Organizer.
    List<PasswordResetToken> findByOrganizer(Organizer organizer);
}
