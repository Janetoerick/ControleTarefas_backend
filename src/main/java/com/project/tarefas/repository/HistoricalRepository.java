package com.project.tarefas.repository;

import com.project.tarefas.model.Historical;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface HistoricalRepository extends JpaRepository<Historical, Long> {
    
    Optional<Historical> findByDashboardId(Long dashboardId);
}