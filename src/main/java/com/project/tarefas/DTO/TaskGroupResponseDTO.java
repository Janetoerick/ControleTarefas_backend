package com.project.tarefas.DTO;

public record TaskGroupResponseDTO (Long id, String title, Long dashboardId){

    public TaskGroupResponseDTO(){
        this(null, null, null);
    }
}
