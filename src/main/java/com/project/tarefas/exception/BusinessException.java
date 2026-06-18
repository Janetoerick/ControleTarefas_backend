package com.project.tarefas.exception;

public class BusinessException extends RuntimeException{
    
    public BusinessException(String err) {
        super(err);
    }
}
