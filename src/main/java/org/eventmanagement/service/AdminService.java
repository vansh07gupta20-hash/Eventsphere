package org.eventmanagement.service;

import org.eventmanagement.entity.Admin;
import org.eventmanagement.entity.User;
import org.eventmanagement.repository.AdminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    private final AdminRepository adminRepository;
    //private final PasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepository , PasswordEncoder passwordEncoder){
        this.adminRepository = adminRepository;
        //this.passwordEncoder = passwordEncoder;
    }
    public Admin saveAdmin(Admin admin){
        //admin.setAdminPassword(passwordEncoder.encode(admin.getAdminPassword()));
        return adminRepository.save(admin);
        //saveAdmin() → Create
    }
    public Optional<Admin> findAdminById(Integer adminId){
        return adminRepository.findById(adminId);
        //findAdminById() → Read by ID
    }
    public Optional<Admin> findAdminByEmail(String adminEmail){
        return adminRepository.findByAdminEmail(adminEmail);
        //findAdminByEmail() → Read by Email
    }
    public void deleteAdminById(Integer adminId){
        adminRepository.deleteById(adminId);
        //deleteAdminById() → Delete
    }
    public List<Admin> findAllAdmins(){
        return adminRepository.findAll();
        //findAllAdmins() → Read all
    }
    public boolean existsAdminById(Integer adminId){
        return adminRepository.existsById(adminId);
        //existsAdminById() → Check existence
    }
    public Admin updateAdmin(Admin admin){
        //admin.setAdminPassword(passwordEncoder.encode(admin.getAdminPassword()));
        return adminRepository.save(admin);
        //updateAdmin() → Update
    }
    public Optional<Admin> authenticateAdmin(String adminEmail, String rawPassword){
        Optional<Admin> adminOptional = adminRepository.findByAdminEmail(adminEmail);
        if (adminOptional.isEmpty()) {
            return Optional.empty();
        }
        Admin admin = adminOptional.get();
        //if (passwordEncoder.matches(rawPassword, admin.getAdminPassword())) {
        if (rawPassword.equals(admin.getAdminPassword())) {
            return Optional.of(admin);
        }
        return Optional.empty();
    }
    public boolean updateAdminPassword(
            String adminEmail,
            String currentPassword,
            String newPassword) {

        Optional<Admin> adminOptional =
                adminRepository.findByAdminEmail(adminEmail);

        if (adminOptional.isEmpty()) {
            return false;
        }

        Admin admin = adminOptional.get();

//        if (!passwordEncoder.matches(
//                currentPassword, admin.getAdminPassword())) {
//            return false;
//        }
        if (!currentPassword.equals(admin.getAdminPassword())) {
            return false;
        }

        if (newPassword.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters");
        }

        //admin.setAdminPassword(passwordEncoder.encode(newPassword));
        admin.setAdminPassword(newPassword);
        adminRepository.save(admin);

        return true;
    }
    public Admin updateAdminProfile(Admin admin) {
        return adminRepository.save(admin);
    }
}
