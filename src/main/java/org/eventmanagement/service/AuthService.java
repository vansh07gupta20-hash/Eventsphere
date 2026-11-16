package org.eventmanagement.service;

import org.eventmanagement.dto.LoginDTO;
import org.eventmanagement.entity.User;
import org.eventmanagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public Optional<User> login(LoginDTO dto) {
        Optional<User> userOptional = userRepository.findByUserEmail(dto.getUserEmail());
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }
        User user = userOptional.get();
        if (!passwordEncoder.matches(dto.getUserPassword(), user.getUserPassword())) {
            return Optional.empty();
        }
        if (!"ACTIVE".equals(user.getUserAccountStatus())) {
            return Optional.empty();
        }
        return Optional.of(user);
    }
}