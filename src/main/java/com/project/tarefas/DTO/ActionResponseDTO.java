package com.project.tarefas.DTO;

import java.time.LocalDateTime;

public record ActionResponseDTO(
    Long id,
    String description,
    LocalDateTime timestamp,
    String userName
) {}