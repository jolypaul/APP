package com.mycompany.skilltest.service.dto;

import java.io.Serializable;

/**
 * Response returned when a password reset is requested.
 */
public class PasswordResetInitResponseDTO implements Serializable {

    private boolean emailSent;

    private String resetLink;

    public boolean isEmailSent() {
        return emailSent;
    }

    public void setEmailSent(boolean emailSent) {
        this.emailSent = emailSent;
    }

    public String getResetLink() {
        return resetLink;
    }

    public void setResetLink(String resetLink) {
        this.resetLink = resetLink;
    }
}
