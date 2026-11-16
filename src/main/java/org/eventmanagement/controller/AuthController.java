package org.eventmanagement.controller;

import jakarta.validation.Valid;
import org.eventmanagement.dto.LoginDTO;
import org.eventmanagement.dto.LoginResponseDTO;
import org.eventmanagement.entity.User;
import org.eventmanagement.service.AuthService;
import org.eventmanagement.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO dto) {
        Optional<User> userOptional = authService.login(dto);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = jwtService.generateToken(user);
            LoginResponseDTO response = new LoginResponseDTO();
            response.setUserId(user.getUserId());
            response.setUserName(user.getUserName());
            response.setUserEmail(user.getUserEmail());
            response.setMessage("Login successful");
            response.setToken(token);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }
}