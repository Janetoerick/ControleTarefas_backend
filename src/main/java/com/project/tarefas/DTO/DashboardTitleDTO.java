package com.project.tarefas.DTO;

import jakarta.validation.constraints.NotBlank;

public class DashboardTitleDTO {

	@NotBlank(message = "O titulo do dashboard eh obrigatorio")
	private String title;

	public DashboardTitleDTO(@NotBlank(message = "O titulo do dashboard eh obrigatorio") String title) {
		super();
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
