package com.project.tarefas.DTO;

import java.sql.Date;

import jakarta.validation.constraints.NotBlank;

public class TaskCreateDTO {
    
    @NotBlank(message = "O título da tarefa não pode estar vazio")
    String title;
    
    String description;
    
    Long taskGroupId;
    
    // Opcional: datas iniciais ou prioridade
    Date date_init;
    Date date_finish;
    String priority; // Recebido como String para converter para Enum no Service

    
    public TaskCreateDTO() {
    }

    public TaskCreateDTO(@NotBlank(message = "O título da tarefa não pode estar vazio") String title,
            String description, Long taskGroupId) {
        this.title = title;
        this.description = description;
        this.taskGroupId = taskGroupId;
    }

    public TaskCreateDTO(@NotBlank(message = "O título da tarefa não pode estar vazio") String title,
            String description, Long taskGroupId, Date date_init, Date date_finish, String priority) {
        this.title = title;
        this.description = description;
        this.taskGroupId = taskGroupId;
        this.date_init = date_init;
        this.date_finish = date_finish;
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Long getTaskGroupId() {
        return taskGroupId;
    }
    public void setTaskGroupId(Long taskGroupId) {
        this.taskGroupId = taskGroupId;
    }
    public Date getDate_init() {
        return date_init;
    }
    public void setDate_init(Date date_init) {
        this.date_init = date_init;
    }
    public Date getDate_finish() {
        return date_finish;
    }
    public void setDate_finish(Date date_finish) {
        this.date_finish = date_finish;
    }
    public String getPriority() {
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }

    
}
