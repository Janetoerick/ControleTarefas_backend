package com.project.tarefas.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    
    private String name;
	
	@JsonIgnore
	@Column(nullable = false)
	private String password;
	
	@OneToMany(mappedBy = "user")
	private Set<Dashboard> dashboards;
	
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private Set<Comment> comments;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
	@JoinTable(name = "dashboard_user",
	joinColumns = @JoinColumn(name = "user_id"),
	inverseJoinColumns = @JoinColumn(name = "dashboard_id"))
	private Set<Dashboard> dashboards_team;
	
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private Set<Action> actions;

	public User() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Dashboard> getDashboards() {
		return dashboards;
	}

	public void setDashboards(Set<Dashboard> dashboards) {
		this.dashboards = dashboards;
	}

	public Set<Comment> getComments() {
		return comments;
	}

	public void setComments(Set<Comment> comments) {
		this.comments = comments;
	}

	public Set<Dashboard> getDashboards_team() {
		return dashboards_team;
	}

	public void setDashboards_team(Set<Dashboard> dashboards_team) {
		this.dashboards_team = dashboards_team;
	}

	public Set<Action> getActions() {
		return actions;
	}

	public void setActions(Set<Action> actions) {
		this.actions = actions;
	}
	
	
}
