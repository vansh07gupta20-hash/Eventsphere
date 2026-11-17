
package org.eventmanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Password_Reset_Tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Reset_Token_ID")
    private Integer resetTokenId;

    @ManyToOne
    @JoinColumn(name = "User_ID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "Organizer_ID")
    private Organizer organizer;

    @Column(name = "Reset_Token", nullable = false, unique = true)
    private String resetToken;

    @Column(name = "Reset_Token_Expiry", nullable = false)
    private LocalDateTime resetTokenExpiry;

    @Column(name = "Reset_Token_Used", nullable = false)
    private Boolean resetTokenUsed = false;

    public PasswordResetToken() {
    }

    public Integer getResetTokenId() {
        return resetTokenId;
    }

    public void setResetTokenId(Integer resetTokenId) {
        this.resetTokenId = resetTokenId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Organizer getOrganizer() {
        return organizer;
    }

    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getResetTokenExpiry() {
        return resetTokenExpiry;
    }

    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }

    public Boolean getResetTokenUsed() {
        return resetTokenUsed;
    }

    public void setResetTokenUsed(Boolean resetTokenUsed) {
        this.resetTokenUsed = resetTokenUsed;
    }
}
