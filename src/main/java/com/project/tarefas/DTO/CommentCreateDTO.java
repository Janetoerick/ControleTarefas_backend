package com.project.tarefas.DTO;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateDTO(
    @NotBlank(message = "O conteúdo do comentário não pode estar vazio")
    String comment
) {}