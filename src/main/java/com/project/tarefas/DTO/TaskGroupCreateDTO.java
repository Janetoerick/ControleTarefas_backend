package com.project.tarefas.DTO;

import jakarta.validation.constraints.NotBlank;

public class TaskGroupCreateDTO {

    @NotBlank(message = "O titulo do grupo é obrigatório")
    String title;

    public TaskGroupCreateDTO(@NotBlank(message = "O titulo do grupo é obrigatório") String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
