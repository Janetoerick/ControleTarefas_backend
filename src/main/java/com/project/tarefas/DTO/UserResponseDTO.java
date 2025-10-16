package com.project.tarefas.DTO;

public class UserResponseDTO {
	
	private Integer id;
	private String username;
    private String email;
    private String name;
    
	public UserResponseDTO(Integer id, String username, String email, String name) {
		super();
		this.id = id;
		this.username = username;
		this.email = email;
		this.name = name;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
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
    
    
}
