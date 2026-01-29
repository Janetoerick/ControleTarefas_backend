package com.project.tarefas.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordChangeDTO (
    @NotBlank(message = "A senha atual é obrigatória")
    String currentPassword,

    @NotBlank(message = "A nova senha é obrigatória")
    @Size(min = 8, max = 30, message = "A senha deve ter entre 8 e 30 caracteres")
    String newPassword,

    @NotBlank(message = "A confirmação da senha é obrigatória")
    String confirmation
){}
