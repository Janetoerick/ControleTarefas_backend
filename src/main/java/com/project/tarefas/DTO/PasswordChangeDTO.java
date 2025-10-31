package com.project.tarefas.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordChangeDTO {

    @NotBlank(message = "A senha atual é obrigatória")
    private String currentPassword;

    @NotBlank(message = "A nova senha é obrigatória")
    @Size(min = 8, max = 30, message = "A senha deve ter entre 8 e 30 caracteres")
    private String newPassword;

    @NotBlank(message = "A confirmação da senha é obrigatória")
    private String confirmation;

    public PasswordChangeDTO() {
    }

    public PasswordChangeDTO(String currentPassword, String newPassword, String confirmation) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmation = confirmation;
    }

    // Getters e Setters
    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(String confirmation) {
        this.confirmation = confirmation;
    }

    // Método utilitário para verificar se as senhas coincidem
    public boolean isNewPasswordMatchingConfirmation() {
        return newPassword != null && newPassword.equals(confirmation);
    }
}
