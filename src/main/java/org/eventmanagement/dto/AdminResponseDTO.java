
package org.eventmanagement.dto;

public class AdminResponseDTO {

    private Integer adminId;
    private String adminName;
    private String adminEmail;

    public AdminResponseDTO() {
    }

    public AdminResponseDTO(
            Integer adminId,
            String adminName,
            String adminEmail) {
        this.adminId = adminId;
        this.adminName = adminName;
        this.adminEmail = adminEmail;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }
}