package com.project.tarefas.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TagCreateDTO(
    @NotBlank(message = "O nome da tag é obrigatório")
    String label,

    @NotBlank(message = "A cor é obrigatória")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "A cor deve ser um código Hexadecimal válido")
    String color
) {}