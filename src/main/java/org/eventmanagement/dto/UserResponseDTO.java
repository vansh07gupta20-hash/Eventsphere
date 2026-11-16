package org.eventmanagement.dto;

import java.time.LocalDate;

public class UserResponseDTO {

    private Integer userId;
    private String userName;
    private String userEmail;
    private String userPhoneNumber;
    private String userAccountStatus;
    private LocalDate userAccountCreationDate;

    public UserResponseDTO(){

    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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
