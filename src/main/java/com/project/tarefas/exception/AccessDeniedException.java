package com.project.tarefas.exception;

public class AccessDeniedException extends RuntimeException{
	public AccessDeniedException () {
		super("Usuario sem permissao para operacao...");
	}
}
