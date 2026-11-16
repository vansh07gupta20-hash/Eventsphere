
package org.eventmanagement.dto;

import org.eventmanagement.entity.Organizer;

public class OrganizerResponseDTO {

    private Integer organizerId;
    private String organizerName;
    private String organizerEmail;
    private String organizerPhoneNumber;
    private String organizerAccountStatus;

    public OrganizerResponseDTO(Organizer organizer) {
        this.organizerId = organizer.getOrganizerId();
        this.organizerName = organizer.getOrganizerName();
        this.organizerEmail = organizer.getOrganizerEmail();
        this.organizerPhoneNumber = organizer.getOrganizerPhoneNumber();
        this.organizerAccountStatus = organizer.getOrganizerAccountStatus();
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

    public String getOrganizerPhoneNumber() {
        return organizerPhoneNumber;
    }

    public String getOrganizerAccountStatus() {
        return organizerAccountStatus;
    }
}