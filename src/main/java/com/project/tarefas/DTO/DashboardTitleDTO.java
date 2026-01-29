package com.project.tarefas.DTO;

import jakarta.validation.constraints.NotBlank;

public record DashboardTitleDTO (
	@NotBlank(message = "O titulo do dashboard eh obrigatorio") 
	String title
){}
