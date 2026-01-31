package com.project.tarefas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.tarefas.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>{
    
    List<Comment> findByTaskIdOrderByCreatedAtDesc(Long taskId);
}
