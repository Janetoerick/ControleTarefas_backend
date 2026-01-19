package com.project.tarefas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.tarefas.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long>{
    
}
