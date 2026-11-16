package org.eventmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "User_ID")
    private Integer userId;

    @NotBlank
    @Column(name = "User_Name")
    private String userName;

    @NotBlank
    @Email
    @Column(name = "User_Email")
    private String userEmail;

    @NotBlank
    @Size(min = 8, message = "Password must contain at least 8 characters")
    @Column(name = "User_Password")
    private String userPassword;

    @Pattern(
            regexp = "^[6-9][0-9]{9}$",   //only valid for india phone number it will change according to countries
            message = "Enter a valid mobile number"
    )
    @Column(name = "User_Phone_Number")
    private String userPhoneNumber;

    @Pattern(
            regexp = "ACTIVE|BLOCKED",
            message = "Account status must be ACTIVE or BLOCKED"
    )
    @Column(name = "User_Account_Status")
    private String userAccountStatus = "ACTIVE";

    @Column(name = "User_Account_Creation_Date")
    private LocalDate userAccountCreationDate = LocalDate.now();

    public User(){

    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserAccountStatus() {
        return userAccountStatus;
    }

    public void setUserAccountStatus(String userAccountStatus) {
        this.userAccountStatus = userAccountStatus;
    }

    public LocalDate getUserAccountCreationDate() {
        return userAccountCreationDate;
    }

    public void setUserAccountCreationDate(LocalDate userAccountCreationDate) {
        this.userAccountCreationDate = userAccountCreationDate;
    }
}
