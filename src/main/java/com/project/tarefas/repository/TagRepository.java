package com.project.tarefas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.tarefas.model.Tag;

public interface TagRepository extends JpaRepository<Tag, Long>{
    
    List<Tag> findByDashboardId(Long dashboardId);
    
}
