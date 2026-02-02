package com.project.tarefas.model;

import java.sql.Time;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "action")
public class Action {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(nullable = false, columnDefinition = "TEXT")
	private String description;
	
	@CreationTimestamp
	@Column(name = "timestamp", nullable = false, updatable = false)
	private LocalDateTime timestamp;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "historical_id")
	private Historical historical;

	public Action() {
	}

	public Action(String description, User user, Historical historical) {
		this.description = description;
		this.user = user;
		this.historical = historical;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Historical getHistorical() {
		return historical;
	}

	public void setHistorical(Historical historical) {
		this.historical = historical;
	}
	
	
}
