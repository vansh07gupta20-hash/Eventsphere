
package org.eventmanagement.controller;

import jakarta.validation.Valid;
import org.eventmanagement.dto.AdminCreateRequestDTO;
import org.eventmanagement.dto.AdminRequestDTO;
import org.eventmanagement.dto.AdminResponseDTO;
import org.eventmanagement.entity.Admin;
import org.eventmanagement.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admins")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    private AdminResponseDTO toResponse(Admin admin) {
        return new AdminResponseDTO(
                admin.getAdminId(),
                admin.getAdminName(),
                admin.getAdminEmail()
        );
    }

    @GetMapping("/{adminId}")
    public ResponseEntity<AdminResponseDTO> getAdminById(
            @PathVariable Integer adminId) {

        Optional<Admin> admin = adminService.findAdminById(adminId);

        return admin.map(value -> ResponseEntity.ok(toResponse(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<AdminResponseDTO> getAllAdmin() {
        return adminService.findAllAdmins()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/email/{adminEmail}")
    public ResponseEntity<AdminResponseDTO> getAdminByEmail(
            @PathVariable String adminEmail) {

        Optional<Admin> admin =
                adminService.findAdminByEmail(adminEmail);

        return admin.map(value -> ResponseEntity.ok(toResponse(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<?> createAdmin(
            @Valid @RequestBody AdminCreateRequestDTO request) {

        if (adminService.findAdminByEmail(request.getAdminEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Admin admin = new Admin();
        admin.setAdminName(request.getAdminName());
        admin.setAdminEmail(request.getAdminEmail());
        admin.setAdminPassword(request.getAdminPassword());

        Admin savedAdmin = adminService.saveAdmin(admin);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(savedAdmin));
    }
    @PutMapping("/{adminId}")
    public ResponseEntity<?> updateAdmin(
            @PathVariable Integer adminId,
            @Valid @RequestBody AdminRequestDTO request) {

        Optional<Admin> existingAdminOptional =
                adminService.findAdminById(adminId);

        if (existingAdminOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Admin existingAdmin = existingAdminOptional.get();

        Optional<Admin> emailOwner =
                adminService.findAdminByEmail(request.getAdminEmail());

        if (emailOwner.isPresent()
                && !emailOwner.get().getAdminId().equals(adminId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        existingAdmin.setAdminName(request.getAdminName());
        existingAdmin.setAdminEmail(request.getAdminEmail());

        Admin updatedAdmin = adminService.updateAdminProfile(existingAdmin);

        return ResponseEntity.ok(toResponse(updatedAdmin));
    }
    @DeleteMapping("/{adminId}")
    public ResponseEntity<Void> deleteAdmin(
            @PathVariable Integer adminId) {

        if (adminService.existsAdminById(adminId)) {
            adminService.deleteAdminById(adminId);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
    @PutMapping("/update-password")
    public ResponseEntity<String> updateAdminPassword(
            @RequestBody Map<String, String> request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof Admin loggedInAdmin)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        if (currentPassword == null || currentPassword.isBlank()
                || newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Current password and new password are required");
        }

        boolean updated = adminService.updateAdminPassword(
                loggedInAdmin.getAdminEmail(),
                currentPassword,
                newPassword
        );

        if (updated) {
            return ResponseEntity.ok(
                    "Admin password updated successfully");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid credentials");
    }
}