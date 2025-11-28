package com.project.tarefas.exception;

public class AccessDeniedException extends Exception{
	public AccessDeniedException () {
		super("Usuario sem permissao para operacao...");
	}
}
