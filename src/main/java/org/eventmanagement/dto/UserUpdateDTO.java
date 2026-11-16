package org.eventmanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserUpdateDTO {

    @NotBlank
    @Size(max = 50,message = "Name must not exceed 50 characters")
    private String userName;

    @NotBlank
    @Email
    private String userEmail;

    @Pattern(
            regexp = "^[6-9][0-9]{9}$",   //only valid for india phone number it will change according to countries
            message = "Enter a valid mobile number"
    )
    private String userPhoneNumber;

    public UserUpdateDTO(){

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
}
