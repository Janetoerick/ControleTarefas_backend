package com.project.tarefas.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.tarefas.model.Dashboard;
import com.project.tarefas.model.User;

public interface DashboardRepository extends JpaRepository<Dashboard, Long> {

	Set<Dashboard> findByUser(User user);
}
