
package org.eventmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UpdateUserAccountStatusDTO {

    @NotBlank(message = "Status is required")
    @Pattern(
            regexp = "ACTIVE|BLOCKED",
            message = "Status must be ACTIVE or BLOCKED"
    )
    private String status;

    public UpdateUserAccountStatusDTO() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}