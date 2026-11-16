package org.eventmanagement.controller;

import jakarta.validation.Valid;
import org.eventmanagement.dto.*;
import org.eventmanagement.entity.Admin;
import org.eventmanagement.entity.PasswordResetToken;
import org.eventmanagement.entity.User;
import org.eventmanagement.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Integer userId, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            User loggedInUser = (User) principal;
            if (!loggedInUser.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to view another user's profile");
            }
        } else if (!(principal instanceof Admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        Optional<User> userOptional = userService.findUserById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOptional.get();
            UserResponseDTO response = new UserResponseDTO();
            response.setUserId(user.getUserId());
            response.setUserName(user.getUserName());
            response.setUserEmail(user.getUserEmail());
            response.setUserPhoneNumber(user.getUserPhoneNumber());
            response.setUserAccountStatus(user.getUserAccountStatus());
            response.setUserAccountCreationDate(user.getUserAccountCreationDate());
            return ResponseEntity.ok(response);
    }
    @GetMapping
    public List<UserResponseDTO> getAllUsers(){
        List<User> users = userService.findAllUsers();
        List<UserResponseDTO> responseList = new java.util.ArrayList<>();
        for (User user : users){
            UserResponseDTO response = new UserResponseDTO();

            response.setUserId(user.getUserId());
            response.setUserName(user.getUserName());
            response.setUserEmail(user.getUserEmail());
            response.setUserPhoneNumber(user.getUserPhoneNumber());
            response.setUserAccountStatus(user.getUserAccountStatus());
            response.setUserAccountCreationDate(user.getUserAccountCreationDate());
            responseList.add(response);
        }
        return responseList;
    }
    @GetMapping("/email/{userEmail}")
    public ResponseEntity<?> getUserByEmail(
            @PathVariable String userEmail,
            Authentication authentication) {

        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            User loggedInUser = (User) principal;

            if (!loggedInUser.getUserEmail().equalsIgnoreCase(userEmail)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You are not allowed to view another user's profile");
            }
        } else if (!(principal instanceof Admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied");
        }

        Optional<User> userOptional = userService.findUserByEmail(userEmail);

        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOptional.get();

        UserResponseDTO response = new UserResponseDTO();
        response.setUserId(user.getUserId());
        response.setUserName(user.getUserName());
        response.setUserEmail(user.getUserEmail());
        response.setUserPhoneNumber(user.getUserPhoneNumber());
        response.setUserAccountStatus(user.getUserAccountStatus());
        response.setUserAccountCreationDate(user.getUserAccountCreationDate());

        return ResponseEntity.ok(response);
    }
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRegistrationDTO request) {
        if (userService.findUserByEmail(request.getUserEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }
        User user = new User();
        user.setUserName(request.getUserName());
        user.setUserEmail(request.getUserEmail());
        user.setUserPassword(request.getUserPassword());
        user.setUserPhoneNumber(request.getUserPhoneNumber());
        user.setUserAccountStatus("ACTIVE");
        User savedUser = userService.saveUser(user);

        UserResponseDTO response = new UserResponseDTO();
        response.setUserId(savedUser.getUserId());
        response.setUserName(savedUser.getUserName());
        response.setUserEmail(savedUser.getUserEmail());
        response.setUserPhoneNumber(savedUser.getUserPhoneNumber());
        response.setUserAccountStatus(savedUser.getUserAccountStatus());
        response.setUserAccountCreationDate(savedUser.getUserAccountCreationDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Integer userId, @Valid @RequestBody UserUpdateDTO updateDTO, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof User){
            User loggedInUser = (User) principal;
            if (!loggedInUser.getUserId().equals(userId)){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to update another user's profile");
            }
        } else if (!(principal instanceof Admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        Optional<User> updatedUserOptional = userService.updateUserProfile(userId, updateDTO);
        if (updatedUserOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found or email already exits");
        }
            User user = updatedUserOptional.get();
            UserResponseDTO response = new UserResponseDTO();
            response.setUserId(user.getUserId());
            response.setUserName(user.getUserName());
            response.setUserEmail(user.getUserEmail());
            response.setUserPhoneNumber(user.getUserPhoneNumber());
            response.setUserAccountStatus(user.getUserAccountStatus());
            response.setUserAccountCreationDate(user.getUserAccountCreationDate());
            return ResponseEntity.ok(response);
    }

@DeleteMapping("/{userId}")
public ResponseEntity<?> deleteUser(@PathVariable Integer userId, Authentication authentication) {
    Object principal = authentication.getPrincipal();
    if (principal instanceof User) {
        User loggedInUser = (User) principal;
        if (!loggedInUser.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to delete another user's account");
        }
    } else if (!(principal instanceof Admin)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
    }
    if (userService.existsUserById(userId)) {
        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
}
    @PutMapping("/{userId}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable Integer userId , @Valid @RequestBody ChangePasswordDTO dto, Authentication authentication){
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN) .body("Only users can change their own password");
        }
        User loggedInUser = (User) principal;
        if (!loggedInUser.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN) .body("You are not allowed to change another user's password");
        }
        Optional<User> result = userService.changeUserPassword(userId , dto);
        if (result.isPresent()) {
            PasswordChangeResponseDTO response = new PasswordChangeResponseDTO("Password changed successfully");
            return ResponseEntity.ok(response);
        }
        else {
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<PasswordResetResponseDTO> forgotPassword(@Valid @RequestBody ForgotPasswordDTO dto) {
        userService.generatePasswordResetToken(dto.getUserEmail());
        PasswordResetResponseDTO response = new PasswordResetResponseDTO("If the email is registered, password reset instructions will be sent.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDTO dto) {

        Optional<User> result = userService.resetUserPassword(
                dto.getResetToken(),
                dto.getNewPassword(),
                dto.getConfirmPassword()
        );

        if (result.isPresent()) {
            PasswordResetResponseDTO response =
                    new PasswordResetResponseDTO("Password reset successfully");

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/{userId}/account-status")
    public ResponseEntity<?> updateUserAccountStatus(@PathVariable Integer userId, @Valid @RequestBody UpdateUserAccountStatusDTO request, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admins can update user account status");
        }
        Optional<User> updatedUserOptional = userService.updateUserAccountStatus(userId, request.getStatus());
        if (updatedUserOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found or invalid status");
        }
        User user = updatedUserOptional.get();
        UserResponseDTO response = new UserResponseDTO();
        response.setUserId(user.getUserId());
        response.setUserName(user.getUserName());
        response.setUserEmail(user.getUserEmail());
        response.setUserPhoneNumber(user.getUserPhoneNumber());
        response.setUserAccountStatus(user.getUserAccountStatus());
        response.setUserAccountCreationDate(user.getUserAccountCreationDate());
        return ResponseEntity.ok(response);
    }
}
