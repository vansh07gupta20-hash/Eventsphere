package org.eventmanagement.repository;

import org.eventmanagement.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    public Optional<Admin> findByAdminEmail(String adminEmail);
}
