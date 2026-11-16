package org.eventmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "organizers")
public class Organizer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Organizer_ID")
    private Integer organizerId;

    @NotBlank
    @Column(name = "Organizer_Name")
    private String organizerName;

    @NotBlank
    @Email
    @Column(name = "Organizer_Email")
    private String organizerEmail;

    @NotBlank
    @Size(min = 8, message = "Password must contain at least 8 characters")
    @Column(name = "Organizer_Password")
    private String organizerPassword;

    @Pattern(
            regexp = "^[6-9][0-9]{9}$",   //only valid for india phone number it will change according to countries
            message = "Enter a valid mobile number"
    )
    @Column(name = "Organizer_Phone_Number")
    private String organizerPhoneNumber;

    @Pattern(
            regexp = "ACTIVE|BLOCKED",
            message = "Account status must be ACTIVE or BLOCKED"
    )
    @Column(name = "Organizer_Account_Status")
    private String organizerAccountStatus = "ACTIVE";

    @Column(name = "Organizer_Account_Creation_Date")
    private LocalDate organizerAccountCreationDate = LocalDate.now();

    public Organizer(){

    }

    public Integer getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Integer organizerId) {
        this.organizerId = organizerId;
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

    public LocalDate getOrganizerAccountCreationDate() {
        return organizerAccountCreationDate;
    }

    public void setOrganizerAccountCreationDate(LocalDate organizerAccountCreationDate) {
        this.organizerAccountCreationDate = organizerAccountCreationDate;
    }
}
