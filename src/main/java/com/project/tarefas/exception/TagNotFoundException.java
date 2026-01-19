package com.project.tarefas.exception;

public class TagNotFoundException extends Exception{
    
    public TagNotFoundException() {
        super("Tag não encontrada.");
    }
}
