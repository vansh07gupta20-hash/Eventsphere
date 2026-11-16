package org.eventmanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class OrganizerLoginDTO {

    @NotBlank(message = "Organizer email is required")
    @Email(message = "Enter a valid email")
    private String organizerEmail;

    @NotBlank(message = "Password is required")
    private String organizerPassword;

    public OrganizerLoginDTO() {
    }

    public String getOrganizerEmail() {
        return organizerEmail;
    }

    public void setOrganizerEmail(String organizerEmail) {
        this.organizerEmail = organizerEmail;
    }

    public String getOrganizerPassword() {
        return organizerPassword;
    }

    public void setOrganizerPassword(String organizerPassword) {
        this.organizerPassword = organizerPassword;
    }
}