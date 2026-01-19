package com.project.tarefas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.tarefas.model.Tag;

public interface TagRepository extends JpaRepository<Tag, Long>{
    
}
