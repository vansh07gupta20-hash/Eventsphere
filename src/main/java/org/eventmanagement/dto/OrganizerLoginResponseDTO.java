package org.eventmanagement.dto;

public class OrganizerLoginResponseDTO {

    private Integer organizerId;
    private String organizerName;
    private String organizerEmail;
    private String message;
    private String token;

    public OrganizerLoginResponseDTO(
            Integer organizerId,
            String organizerName,
            String organizerEmail,
            String message,
            String token) {
        this.organizerId = organizerId;
        this.organizerName = organizerName;
        this.organizerEmail = organizerEmail;
        this.message = message;
        this.token = token;
    }

    public Integer getOrganizerId() {
        return organizerId;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public String getOrganizerEmail() {
        return organizerEmail;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }
}