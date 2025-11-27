package com.project.tarefas.DTO;

import java.util.Set;

import com.project.tarefas.model.Task;
import com.project.tarefas.model.TaskGroup;

public class DashboardResponseDTO {

	private Long id;
	
	private String title;
	
	private Set<Task> tasks;
	
	private Long user;
	
	private Set<TaskGroup> taskgroups;
	
	private Set<Long> team;
	
    private Long historical;
    

	public DashboardResponseDTO(Long id, String title, Set<Task> tasks, Long user, Set<TaskGroup> taskgroups,
			Set<Long> team, Long historical) {
		super();
		this.id = id;
		this.title = title;
		this.tasks = tasks;
		this.user = user;
		this.taskgroups = taskgroups;
		this.team = team;
		this.historical = historical;
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

	public Set<Task> getTasks() {
		return tasks;
	}

	public void setTasks(Set<Task> tasks) {
		this.tasks = tasks;
	}

	public Long getUser() {
		return user;
	}

	public void setUser(Long user) {
		this.user = user;
	}

	public Set<TaskGroup> getTaskgroups() {
		return taskgroups;
	}

	public void setTaskgroups(Set<TaskGroup> taskgroups) {
		this.taskgroups = taskgroups;
	}

	public Set<Long> getTeam() {
		return team;
	}

	public void setTeam(Set<Long> team) {
		this.team = team;
	}

	public Long getHistorical() {
		return historical;
	}

	public void setHistorical(Long historical) {
		this.historical = historical;
	}
    
}
