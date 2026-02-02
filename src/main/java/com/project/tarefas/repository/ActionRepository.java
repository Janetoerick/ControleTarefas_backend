package com.project.tarefas.repository;

import com.project.tarefas.model.Action;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionRepository extends JpaRepository<Action, Long> {
    Page<Action> findByHistoricalIdOrderByTimestampDesc(Long historicalId, Pageable pageable);
}