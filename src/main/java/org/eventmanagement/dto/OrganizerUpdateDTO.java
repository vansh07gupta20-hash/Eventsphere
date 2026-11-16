
package org.eventmanagement.dto;

public class OrganizerUpdateDTO {

    private String organizerName;
    private String organizerEmail;
    private String organizerPassword;
    private String organizerPhoneNumber;
    private String organizerAccountStatus;

    public OrganizerUpdateDTO() {
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
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

    public String getOrganizerPhoneNumber() {
        return organizerPhoneNumber;
    }

    public void setOrganizerPhoneNumber(String organizerPhoneNumber) {
        this.organizerPhoneNumber = organizerPhoneNumber;
    }

    public String getOrganizerAccountStatus() {
        return organizerAccountStatus;
    }

    public void setOrganizerAccountStatus(String organizerAccountStatus) {
        this.organizerAccountStatus = organizerAccountStatus;
    }
}