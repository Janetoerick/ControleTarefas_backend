package com.project.tarefas.exception;

public class InvalidPasswordException extends RuntimeException {
	public InvalidPasswordException(String err) {
		super(err);
	}
}
