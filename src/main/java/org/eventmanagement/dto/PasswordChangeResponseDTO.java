package org.eventmanagement.dto;

public class PasswordChangeResponseDTO {
    private String message;

    public PasswordChangeResponseDTO(){

    }

    public PasswordChangeResponseDTO(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
