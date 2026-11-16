
package org.eventmanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "registrations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_registration_user_event",
                        columnNames = {"User_ID", "Event_ID"}
                )
        }
)
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Registration_ID")
    private Integer registrationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "User_ID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Event_ID", nullable = false)
    private Event event;

    @Column(
            name = "Registration_Date",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime registrationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "Registration_Status", nullable = false)
    private RegistrationStatus registrationStatus =
            RegistrationStatus.REGISTERED;

    public Registration() {
    }

    public Integer getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Integer registrationId) {
        this.registrationId = registrationId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public RegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(
            RegistrationStatus registrationStatus) {
        this.registrationStatus = registrationStatus;
    }
}