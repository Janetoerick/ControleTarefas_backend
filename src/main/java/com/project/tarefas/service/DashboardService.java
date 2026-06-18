package com.project.tarefas.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.tarefas.exception.UserNotFoundException;
import com.project.tarefas.mapper.DashboardMapper;
import com.project.tarefas.mapper.TagMapper;
import com.project.tarefas.DTO.DashboardTitleDTO;
import com.project.tarefas.DTO.TagResponseDTO;
import com.project.tarefas.DTO.DashboardResponseDTO;
import com.project.tarefas.exception.AccessDeniedException;
import com.project.tarefas.exception.DashboardNotFoundException;
import com.project.tarefas.model.Dashboard;
import com.project.tarefas.model.User;
import com.project.tarefas.repository.DashboardRepository;
import com.project.tarefas.repository.UserRepository;

@Service 
public class DashboardService {

	private final DashboardRepository dashboardRepository;
	private final UserRepository userRepository;
	private final DashboardMapper dashboardMapper;
	private final TagMapper tagMapper;

	public DashboardService(DashboardRepository dashboardRepository, UserRepository userRepository
			, DashboardMapper dashboardMapper, TagMapper tagMapper) {
		super();
		this.dashboardRepository = dashboardRepository;
		this.userRepository = userRepository;
		this.dashboardMapper = dashboardMapper;
		this.tagMapper = tagMapper;
	}
	
	// Cria um novo Dashboard
	public DashboardResponseDTO createDashboard(Long user, DashboardTitleDTO dto) throws UserNotFoundException {
		
		User user_all = userRepository.findById(user)
				.orElseThrow(() -> new UserNotFoundException("Usuário não encontrado..."));
		
		Dashboard new_dashboard = new Dashboard(dto.title(), user_all);
		
		dashboardRepository.save(new_dashboard);
		
		return dashboardMapper.toResponseDTO(new_dashboard);
	}
	
	// Deleta um novo Dashboard
	public void deleteDashboard(Long user, Long dashboard) throws AccessDeniedException, DashboardNotFoundException {
		
		Dashboard dashboard_all = dashboardRepository.findById(dashboard)
				.orElseThrow(() -> new DashboardNotFoundException("Dashboard não existe..."));
		
		if(dashboard_all.getUser().getId() != user) {
			throw new AccessDeniedException();
		}
		
		dashboardRepository.delete(dashboard_all);
	}
	
	// Modifica o titulo de um Dashboard existente
	public DashboardResponseDTO editTitle(Long dashboard, Long user, String title) throws AccessDeniedException, DashboardNotFoundException {
		
		Dashboard dashboard_edit = dashboardRepository.findById(dashboard)
				.orElseThrow(() -> new DashboardNotFoundException("Dashboard não existe..."));
		
		if(dashboard_edit.getUser().getId() != user) {
			throw new AccessDeniedException();
		}
		
		dashboard_edit.setTitle(title);
		Dashboard savedDashboard = dashboardRepository.save(dashboard_edit);
		
		return dashboardMapper.toResponseDTO(savedDashboard);
	}
	
	// Retorna um Todas as informacoes de um Dashboard de acordo com o Id
	public DashboardResponseDTO findDashboardById(Long id) throws DashboardNotFoundException {
		Dashboard dashboard = dashboardRepository.findById(id)
				.orElseThrow(() -> new DashboardNotFoundException("Dashboard não existe..."));
		
		return dashboardMapper.toResponseDTO(dashboard);
	}
	
	// Retorna todos os Dashboards que o usuario id seja dono
	public Set<DashboardResponseDTO> findAllDashboardByUser(Long id) throws UserNotFoundException  {
		
		User user_all = userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("Usuário não encontrado..."));
		
		Set<Dashboard> dashboards = dashboardRepository.findByUser(user_all);
		
		if(dashboards.isEmpty()) {
			return Collections.emptySet();
		}
		
		return dashboardMapper.toResponseAllDTO(dashboards);
	}
	
	// Lista todas as Tags do Dashboard -> DONO e EQUIPE
    @Transactional(readOnly = true)
    public List<TagResponseDTO> listDashboardTags(Long userId, Long dashboardId) throws AccessDeniedException, DashboardNotFoundException {
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new DashboardNotFoundException("Dashboard não existe..."));
        
        validateDashboardAccess(dashboard, userId);
        
        return dashboard.getTags()
                .stream()
                .map(tagMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
 // Verifica se pertence ao Dashboard (Dono ou Equipe)
    private void validateDashboardAccess(Dashboard dashboard, Long userId) throws AccessDeniedException {
        boolean isOwner = dashboard.getUser().getId().equals(userId);
        boolean isMember = dashboard.getTeam() != null && 
                           dashboard.getTeam().stream().anyMatch(u -> u.getId().equals(userId));

        if (!isOwner && !isMember) {
            throw new AccessDeniedException();
        }
    }
	
}
