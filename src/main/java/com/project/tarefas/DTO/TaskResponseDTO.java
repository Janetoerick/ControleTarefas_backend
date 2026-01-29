package com.project.tarefas.DTO;

import java.util.Date;
import java.util.Set;

import com.project.tarefas.model.enums.Priority;
import com.project.tarefas.model.enums.StatusTask;


public record TaskResponseDTO (
    Long id,
	String title,
	String description,
	Date date_init,
	Date date_finish,
	Long dashboardId,
	Set<Long> tags,
	Long taskGroupId,
    StatusTask status,
	Priority priority
){

    
    public TaskResponseDTO() {
        this(null, null, null, null, null, null, null ,null ,null ,null);
    }

    public TaskResponseDTO(String title, String description, Long dashboardId, Long taskGroupId) {
        this(null, title, description, null, null, dashboardId, null ,taskGroupId ,null ,null);
    }

    public TaskResponseDTO(Long id, String title, String description,
            Long dashboardId, Long taskGroupId, StatusTask status, Priority priority) {
        this(id, title, description, null, null, dashboardId, null ,taskGroupId ,status ,priority);
    }

    public TaskResponseDTO(Long id, String title, String description, Date date_init, Date date_finish,
            Long dashboardId, Long taskGroupId, StatusTask status, Priority priority) {
        this(id, title, description, date_init, date_finish, dashboardId, null ,taskGroupId ,status ,priority);
    }
}
