package com.project.tarefas.exception;

public class TagNotFoundException extends RuntimeException{
    
    public TagNotFoundException() {
        super("Tag não encontrada.");
    }
}
