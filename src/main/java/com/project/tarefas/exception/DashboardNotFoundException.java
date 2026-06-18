package com.project.tarefas.exception;

public class DashboardNotFoundException extends RuntimeException{
	public DashboardNotFoundException(String err) {
		super(err);
	}
}
