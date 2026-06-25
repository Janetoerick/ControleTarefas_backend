package com.project.tarefas.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.project.tarefas.model.enums.Priority;
import com.project.tarefas.model.enums.StatusTask;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "task")
public class Task {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String title;
	
	private String description;
	
	private Date date_init;
	
	private Date date_finish;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "dashboard_id")
	private Dashboard dashboard;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
	@JoinTable(name = "task_tag",
	joinColumns = @JoinColumn(name = "task_id"),
	inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private Set<Tag> tags = new HashSet<>();
	
	@OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Comment> comments = new HashSet<>();
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "taskgroup_id")
	private TaskGroup taskGroup;
	
	@Enumerated(EnumType.STRING)
    private StatusTask status;
	
	@Enumerated(EnumType.STRING)
	private Priority priority;

	public Task() {
	}

	public Task(String title, String description, Date date_init, Date date_finish) {
		super();
		this.title = title;
		this.description = description;
		this.date_init = date_init;
		this.date_finish = date_finish;
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

	public Dashboard getDashboard() {
		return dashboard;
	}

	public void setDashboard(Dashboard dashboard) {
		this.dashboard = dashboard;
	}

	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	public Set<Comment> getComments() {
		return comments;
	}

	public void setComments(Set<Comment> comments) {
		this.comments = comments;
	}

	public TaskGroup getTaskGroup() {
		return taskGroup;
	}

	public void setTaskGroup(TaskGroup taskGroup) {
		this.taskGroup = taskGroup;
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
	
	
}
