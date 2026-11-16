package org.eventmanagement.service;

import org.eventmanagement.dto.ChangePasswordDTO;
import org.eventmanagement.dto.UserUpdateDTO;
import org.eventmanagement.entity.PasswordResetToken;
import org.eventmanagement.entity.User;
import org.eventmanagement.repository.PasswordResetTokenRepository;
import org.eventmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private EmailService emailService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }
    public User saveUser(User user){
        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        return userRepository.save(user);
        //saveUser() → Create
    }
    public Optional<User> findUserById(Integer userId){
        return userRepository.findById(userId);
        //findUserById() → Read by ID
    }
    public Optional<User> findUserByEmail(String userEmail){
        return userRepository.findByUserEmail(userEmail);
        //findUserByEmail() → Read by Email
    }
    public void deleteUserById(Integer userId){
        userRepository.deleteById(userId);
        //deleteUserById() → Delete
    }
    public List<User> findAllUsers(){
        return userRepository.findAll();
        //findAllUsers() → Read all
    }
    public boolean existsUserById(Integer userId){
        return userRepository.existsById(userId);
        //existsUserById() → Check existence
    }
    public Optional<User> updateUserProfile(Integer userId, UserUpdateDTO updateDTO) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }
        User user = userOptional.get();
        Optional<User> existingEmailUser = userRepository.findByUserEmail(updateDTO.getUserEmail());
        if (existingEmailUser.isPresent() && !existingEmailUser.get().getUserId().equals(user.getUserId())) {
            return Optional.empty();
        }
            user.setUserName(updateDTO.getUserName());
            user.setUserEmail(updateDTO.getUserEmail());
            user.setUserPhoneNumber(updateDTO.getUserPhoneNumber());
            return Optional.of(userRepository.save(user));
    }
    @Transactional
    public Optional<User> changeUserPassword(Integer userId, ChangePasswordDTO dto){
       Optional<User> userOptional = userRepository.findById(userId);
       if (userOptional.isPresent()){
           User user = userOptional.get();
          if (!passwordEncoder.matches(dto.getCurrentPassword() , user.getUserPassword())) {
              return Optional.empty();
          }
          if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
              return Optional.empty();
          }
          String encodedPassword = passwordEncoder.encode(dto.getNewPassword());
          user.setUserPassword(encodedPassword);
          return Optional.of(userRepository.save(user));
      }
       return Optional.empty();
    }

    public Optional<PasswordResetToken> generatePasswordResetToken(String userEmail) {

        Optional<User> userOptional =
                userRepository.findByUserEmail(userEmail);

        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();

        String token = UUID.randomUUID().toString();

        PasswordResetToken passwordResetToken =
                new PasswordResetToken();

        passwordResetToken.setUser(user);
        passwordResetToken.setResetToken(token);
        passwordResetToken.setResetTokenExpiry(
                LocalDateTime.now().plusMinutes(30)
        );
        passwordResetToken.setResetTokenUsed(false);

        PasswordResetToken savedToken =
                passwordResetTokenRepository.save(passwordResetToken);

        // Send password reset email
        emailService.sendPasswordResetEmail(
                user.getUserEmail(),
                token
        );

        return Optional.of(savedToken);
    }

    @Transactional
    public Optional<User> resetUserPassword(String resetToken, String newPassword, String confirmPassword) {
        Optional<PasswordResetToken> tokenOptional = passwordResetTokenRepository.findByResetToken(resetToken);
        if (tokenOptional.isEmpty()) {
            return Optional.empty();
        }
        PasswordResetToken passwordResetToken = tokenOptional.get();
        if (passwordResetToken.getResetTokenUsed()) {
            return Optional.empty();
        }
        if (passwordResetToken.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return Optional.empty();
        }
        if (newPassword == null || !newPassword.equals(confirmPassword)) {
            return Optional.empty();
        }
        User user = passwordResetToken.getUser();
        user.setUserPassword(passwordEncoder.encode(newPassword));
        User savedUser = userRepository.save(user);
        List<PasswordResetToken> tokens = passwordResetTokenRepository.findByUser(user);
        for (PasswordResetToken token : tokens){
            token.setResetTokenUsed(true);
        }
        passwordResetTokenRepository.saveAll(tokens);
        return Optional.of(savedUser);
    }
    public Optional<User> updateUserAccountStatus(Integer userId , String status){
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()){
            return Optional.empty();
        }
        User user = userOptional.get();
        if ( status == null || !status.equals("ACTIVE") && !status.equals("BLOCKED")){
            return Optional.empty();
        }
        user.setUserAccountStatus(status);
        User updatedUser = userRepository.save(user);
        return Optional.of(updatedUser);
    }
}
