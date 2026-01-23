package com.project.tarefas.model;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "dashboard")
public class Dashboard {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String title;
	
	@OneToMany(mappedBy = "dashboard", fetch = FetchType.LAZY)
	private Set<Task> tasks;

	@OneToMany(mappedBy = "dashboard")
	private Set<Tag> tags;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@OneToMany(mappedBy = "dashboard")
	private Set<TaskGroup> taskgroups;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
	@JoinTable(name = "dashboard_user",
	joinColumns = @JoinColumn(name = "dashboard_id"),
	inverseJoinColumns = @JoinColumn(name = "user_id"))
	private Set<User> team = new java.util.HashSet<>();
	
	@OneToOne(mappedBy = "dashboard", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private Historical historical;
	
	public Dashboard() {
	}

	public Dashboard(String title, User user) {
		super();
		this.title = title;
		this.user = user;
	}

	public Dashboard(Long id, String title, User user) {
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

	public Set<Task> getTasks() {
		return tasks;
	}

	public void setTasks(Set<Task> tasks) {
		this.tasks = tasks;
	}

	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Set<TaskGroup> getTaskgroups() {
		return taskgroups;
	}

	public void setTaskgroups(Set<TaskGroup> taskgroups) {
		this.taskgroups = taskgroups;
	}

	public Set<User> getTeam() {
		return team;
	}

	public void setTeam(Set<User> team) {
		this.team = team;
	}

	public Historical getHistorical() {
		return historical;
	}

	public void setHistorical(Historical historical) {
		this.historical = historical;
	}

	
	
	
}
