package com.project.tarefas.DTO;

public class TaskGroupResponseDTO {
    
    private Long id;
    private String title;
    private Long dashboardId;
    
    public TaskGroupResponseDTO() {
    }
    
    public TaskGroupResponseDTO(Long id, String title, Long dashboardId) {
        this.id = id;
        this.title = title;
        this.dashboardId = dashboardId;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Long getDashboardId() {
        return dashboardId;
    }
    public void setDashboardId(Long dashboardId) {
        this.dashboardId = dashboardId;
    }
    
}
