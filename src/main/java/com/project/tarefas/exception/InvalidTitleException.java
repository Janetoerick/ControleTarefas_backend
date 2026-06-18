package com.project.tarefas.exception;

public class InvalidTitleException extends RuntimeException{
	
	public InvalidTitleException() {
		super("Titulo inválido...");	
	}

}
