package com.project.tarefas.controller;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.project.tarefas.DTO.DashboardResponseDTO;
import com.project.tarefas.DTO.DashboardTitleDTO;
import com.project.tarefas.DTO.TagResponseDTO;
import com.project.tarefas.DTO.TaskGroupResponseDTO;
import com.project.tarefas.config.security.SecurityUserDetails;
import com.project.tarefas.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
	
	private final DashboardService dashboardService;

	public DashboardController(DashboardService dashboardService) {
		super();
		this.dashboardService = dashboardService;
	}
	
	/**
     * Cria um novo dashboard.
     */
	@PostMapping("/user/{userId}")
    public ResponseEntity<DashboardResponseDTO> createDashboard(
        @PathVariable Long userId,
        @RequestBody DashboardTitleDTO request) {
        
        DashboardResponseDTO response = dashboardService.createDashboard(userId, request);
        
        return ResponseEntity.ok(response);
    }
	
	/**
     * Deleta um dashboard.
     */
	@DeleteMapping("/user/{userId}/{dashboardId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)  // Retorna 204 para exclusão bem-sucedida
	public void deleteDashboard(
			@PathVariable Long userId,
			@PathVariable Long dashboardId
			) {
		
		dashboardService.deleteDashboard(userId, dashboardId);
	}
	
	/**
     * Edita o titulo de um dashboard.
     */
	@PatchMapping("/user/{userId}/{dashboardId}/title")
	public DashboardResponseDTO editDashboardTitle(
	        @PathVariable Long userId, 
	        @PathVariable Long dashboardId,
	        @RequestBody DashboardTitleDTO request
			) {
	
        return dashboardService.editTitle(
            dashboardId, 
            userId, 
            request.title()
        );
    }

	/**
     * Acha um dashboard pelo id.
     */
	@GetMapping("/{id}")
	public DashboardResponseDTO findDashboardById(
			@PathVariable Long id) {
		
		return dashboardService.findDashboardById(id);
	}
	
	/**
     * Acha todos os dashboards com o id do criador.
     */
	@GetMapping("/user/{userId}")
	public Set<DashboardResponseDTO> findDashboardByUserId(
			@PathVariable Long userId) {
		
		return dashboardService.findAllDashboardByUser(userId);
	}
	
	/**
     * Lista todas as Tags disponiveis no escopo do Dashboard
     */
    @GetMapping("/{dashboardId}/tags")
    public ResponseEntity<List<TagResponseDTO>> listTagsByDashboard(
            @AuthenticationPrincipal SecurityUserDetails userDetails,
            @PathVariable Long dashboardId) {
        
        List<TagResponseDTO> tags = dashboardService.listDashboardTags(
            userDetails.getUser().getId(), 
            dashboardId
        );
        return ResponseEntity.ok(tags);
    }
    
    /**
     * Lista todas as colunas de um Dashboard.
     * Tanto o PROPRIETÁRIO quanto os membros do TIME podem visualizar.
     */
    @GetMapping("/{dashboardId}")
    public ResponseEntity<List<TaskGroupResponseDTO>> list(
            @PathVariable Long dashboardId,
            @AuthenticationPrincipal SecurityUserDetails userDetails)  {
        
        List<TaskGroupResponseDTO> response = dashboardService.listTaskGroups(userDetails.getUser().getId(), dashboardId);
        return ResponseEntity.ok(response);
    }
	
	
}
