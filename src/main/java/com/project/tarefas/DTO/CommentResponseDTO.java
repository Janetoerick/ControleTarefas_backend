package com.project.tarefas.DTO;

import java.time.LocalDateTime;

public record CommentResponseDTO(
    Long id,
    String content,
    String authorName,
    LocalDateTime createdAt
) {}
