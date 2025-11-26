package com.project.tarefas.service;

import org.springframework.stereotype.Service;

import com.project.tarefas.exception.InvalidTitleException;
import com.project.tarefas.exception.UserNotFoundException;
import com.project.tarefas.exception.DashboardInvalidException;
import com.project.tarefas.model.Dashboard;
import com.project.tarefas.model.User;
import com.project.tarefas.repository.DashboardRepository;
import com.project.tarefas.repository.UserRepository;

@Service
public class DashboardService {

	private final DashboardRepository dashboardRepository;
	private final UserRepository userRepository;

	public DashboardService(DashboardRepository dashboardRepository, UserRepository userRepository) {
		super();
		this.dashboardRepository = dashboardRepository;
		this.userRepository = userRepository;
	}
	
	public Dashboard createDashboard(String title, Long user) throws UserNotFoundException {
		if(title.isEmpty() || title.contains("_")) {
			new InvalidTitleException();
		}
		
		User user_all = userRepository.findById(user)
				.orElseThrow(() -> new UserNotFoundException("Usuário não encontrado..."));
		
		Dashboard new_dashboard = new Dashboard(title, user_all);
		
		dashboardRepository.save(new_dashboard);
		return new_dashboard;
	}
	
	public void deleteDashboard(Long user, Long dashboard) throws UserNotFoundException, DashboardInvalidException {
		
		Dashboard dashboard_all = dashboardRepository.findById(dashboard)
				.orElseThrow(() -> new DashboardInvalidException("Dashboard não existe..."));
		
		if(dashboard_all.getUser().getId() != user) {
			new UserNotFoundException("Usuario sem permissao para deletar este dashboard...");
		}
		
		dashboardRepository.delete(dashboard_all);
	}
	
	public Dashboard editTitle(Long dashboard, Long user, String title) throws UserNotFoundException, DashboardInvalidException{
		if(title.isEmpty() || title.contains("_")) {
			new InvalidTitleException();
		}
		
		Dashboard dashboard_edit = dashboardRepository.findById(dashboard)
				.orElseThrow(() -> new DashboardInvalidException("Dashboard não existe..."));
		
		if(dashboard_edit.getUser().getId() != user) {
			new UserNotFoundException("Usuario sem permissao para deletar este dashboard...");
		}
		
		dashboard_edit.setTitle(title);
		
		return dashboardRepository.save(dashboard_edit);
	}
	
}
