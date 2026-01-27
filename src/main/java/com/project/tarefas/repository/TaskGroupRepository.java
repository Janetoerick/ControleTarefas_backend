package com.project.tarefas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.tarefas.model.TaskGroup;

public interface TaskGroupRepository extends JpaRepository<TaskGroup, Long>{
    
    List<TaskGroup> findByDashboardId(Long dashboardId);
}
