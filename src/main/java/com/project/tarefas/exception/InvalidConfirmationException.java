package com.project.tarefas.exception;

public class InvalidConfirmationException extends RuntimeException{
	public InvalidConfirmationException(String err) {
		super(err);
	}
}
