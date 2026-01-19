package com.project.tarefas.exception;

public class TaskNotFoundException extends Exception{
    
    public TaskNotFoundException() {
		super("Tarefa não encontrada.");	
	}
}
