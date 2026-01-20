package com.project.tarefas.DTO;

import java.util.Date;
import java.util.Set;

import com.project.tarefas.model.Tag;
import com.project.tarefas.model.enums.Priority;
import com.project.tarefas.model.enums.StatusTask;


public class TaskResponseDTO {
    
	private Long id;
	private String title;
	private String description;
	private Date date_init;
	private Date date_finish;
	private Long dashboardId;
	private Set<Long> tags;
	private Long taskGroupId;
    private StatusTask status;
	private Priority priority;

    
    public TaskResponseDTO() {
    }

    public TaskResponseDTO(String title, String description, Long dashboardId, Long taskGroupId) {
        this.title = title;
        this.description = description;
        this.dashboardId = dashboardId;
        this.taskGroupId = taskGroupId;
    }

    public TaskResponseDTO(Long id, String title, String description,
            Long dashboardId, Long taskGroupId, StatusTask status, Priority priority) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dashboardId = dashboardId;
        this.taskGroupId = taskGroupId;
        this.status = status;
        this.priority = priority;
    }

    public TaskResponseDTO(Long id, String title, String description, Date date_init, Date date_finish,
            Long dashboardId, Set<Long> tags, Long taskGroupId, StatusTask status, Priority priority) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date_init = date_init;
        this.date_finish = date_finish;
        this.dashboardId = dashboardId;
        this.tags = tags;
        this.taskGroupId = taskGroupId;
        this.status = status;
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
    public Long getDashboardId() {
        return dashboardId;
    }
    public void setDashboardId(Long dashboardId) {
        this.dashboardId = dashboardId;
    }
    public Set<Long> getTags() {
        return tags;
    }
    public void setTags(Set<Long> tags) {
        this.tags = tags;
    }
    public Long getTaskGroupId() {
        return taskGroupId;
    }
    public void setTaskGroupId(Long taskGroupId) {
        this.taskGroupId = taskGroupId;
    }
    public StatusTask getStatus() {
        return status;
    }
    public void setStatus(StatusTask status) {
        this.status = status;
    }
    public Priority getPriority() {
        return priority;
    }
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    
}
