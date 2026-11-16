package org.eventmanagement.controller;

import org.eventmanagement.dto.AdminLoginDTO;
import org.eventmanagement.dto.AdminLoginResponseDTO;
import org.eventmanagement.entity.Admin;
import org.eventmanagement.service.AdminService;
import org.eventmanagement.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/admin/auth")
public class AdminAuthController {

    private final AdminService adminService;
    private final JwtService jwtService;

    public AdminAuthController(AdminService adminService, JwtService jwtService) {
        this.adminService = adminService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminLoginDTO dto) {


        Optional<Admin> adminOptional = adminService.authenticateAdmin(
                dto.getAdminEmail(),
                dto.getAdminPassword()
        );

        if (adminOptional.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        Admin admin = adminOptional.get();

        String token = jwtService.generateAdminToken(admin);

        AdminLoginResponseDTO response = new AdminLoginResponseDTO();
        response.setAdminId(admin.getAdminId());
        response.setAdminName(admin.getAdminName());
        response.setAdminEmail(admin.getAdminEmail());
        response.setMessage("Admin login successful");
        response.setToken(token);

        return ResponseEntity.ok(response);
    }
}