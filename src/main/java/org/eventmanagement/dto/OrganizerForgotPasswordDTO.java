
package org.eventmanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class OrganizerForgotPasswordDTO {

    @NotBlank
    @Email
    private String organizerEmail;

    public OrganizerForgotPasswordDTO() {
    }

    public String getOrganizerEmail() {
        return organizerEmail;
    }

    public void setOrganizerEmail(String organizerEmail) {
        this.organizerEmail = organizerEmail;
    }
}
