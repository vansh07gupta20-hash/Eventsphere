package org.eventmanagement.repository;

import org.eventmanagement.entity.PasswordResetToken;
import org.eventmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Integer> {

    Optional<PasswordResetToken> findByResetToken(String resetToken);
    List<PasswordResetToken> findByUser(User user);
}