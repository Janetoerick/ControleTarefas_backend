package com.project.tarefas.DTO;

import jakarta.validation.constraints.NotBlank;

public record TaskGroupCreateDTO (
    @NotBlank(message = "O titulo do grupo é obrigatório")
    String title
){}
