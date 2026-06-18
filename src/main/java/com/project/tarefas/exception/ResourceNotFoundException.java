package com.project.tarefas.exception;

public class ResourceNotFoundException extends RuntimeException{
    
    public ResourceNotFoundException(String err) {
        super(err);
    }
}
