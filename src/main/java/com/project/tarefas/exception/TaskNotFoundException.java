package com.project.tarefas.exception;

public class TaskNotFoundException extends RuntimeException{
    
    public TaskNotFoundException() {
		super("Tarefa não encontrada.");	
	}
}
