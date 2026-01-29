package com.project.tarefas.DTO;

import java.sql.Date;

import jakarta.validation.constraints.NotBlank;

public record TaskCreateDTO (
    @NotBlank(message = "O título da tarefa não pode estar vazio")
    String title,
    String description,
    Long taskGroupId,
    Date date_init,
    Date date_finish,
    String priority
){

    public TaskCreateDTO() {
        this(null, null, null, null, null, null);
    }

    public TaskCreateDTO(@NotBlank(message = "O título da tarefa não pode estar vazio") String title,
            String description, Long taskGroupId) {
        this(title, description, taskGroupId, null, null, null);
    }

    public TaskCreateDTO(@NotBlank(message = "O título da tarefa não pode estar vazio") String title,
            Long taskGroupId, String priority) {
        this(title, null, taskGroupId, null, null, priority);
    }
    
}
