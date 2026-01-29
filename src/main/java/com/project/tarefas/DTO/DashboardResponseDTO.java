package com.project.tarefas.DTO;

import java.util.Set;

import com.project.tarefas.model.Task;
import com.project.tarefas.model.TaskGroup;


public class DashboardResponseDTO {

	private Long id;
	private String title;
	private Set<Long> tasks;
	private Set<Long> tags;
	private Long user;
	private Set<Long> taskgroups;
	private Set<Long> team;
    private Long historical;
    
	public DashboardResponseDTO() {
		super();
	}

	public DashboardResponseDTO(Long id, String title, Set<Long> tasks, Set<Long> tags, Long user, Set<Long> taskgroups,
			Set<Long> team, Long historical) {
		this.id = id;
		this.title = title;
		this.tasks = tasks;
		this.tags = tags;
		this.user = user;
		this.taskgroups = taskgroups;
		this.team = team;
		this.historical = historical;
	}

	public DashboardResponseDTO(Long id, String title, Long user) {
		super();
		this.id = id;
		this.title = title;
		this.user = user;
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

	public Set<Long> getTasks() {
		return tasks;
	}

	public void setTasks(Set<Long> tasks) {
		this.tasks = tasks;
	}

	public Long getUser() {
		return user;
	}

	public void setUser(Long user) {
		this.user = user;
	}

	public Set<Long> getTaskgroups() {
		return taskgroups;
	}

	public void setTaskgroups(Set<Long> taskgroups) {
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

	public Set<Long> getTags() {
		return tags;
	}

	public void setTags(Set<Long> tags) {
		this.tags = tags;
	}
    
}
