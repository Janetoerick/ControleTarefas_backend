package com.project.tarefas.exception;

public class UserNotFoundException extends RuntimeException {
	public UserNotFoundException(String err) {
		super(err);
	}
}
