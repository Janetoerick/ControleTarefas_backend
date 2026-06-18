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
import com.project.tarefas.config.security.SecurityUserDetails;
import com.project.tarefas.exception.AccessDeniedException;
import com.project.tarefas.exception.DashboardNotFoundException;
import com.project.tarefas.exception.UserNotFoundException;
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
     * @throws UserNotFoundException 
     */
	@PostMapping("/user/{userId}")
    public ResponseEntity<DashboardResponseDTO> createDashboard(
        @PathVariable Long userId,
        @RequestBody DashboardTitleDTO request) throws UserNotFoundException {
        
        DashboardResponseDTO response = dashboardService.createDashboard(userId, request);
        
        return ResponseEntity.ok(response);
    }
	
	/**
     * Deleta um dashboard.
     * @throws AccessDeniedException 
	 * @throws DashboardNotFoundException 
     */
	@DeleteMapping("/user/{userId}/{dashboardId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)  // Retorna 204 para exclusão bem-sucedida
	public void deleteDashboard(
			@PathVariable Long userId,
			@PathVariable Long dashboardId
			) throws AccessDeniedException, DashboardNotFoundException {
		
		dashboardService.deleteDashboard(userId, dashboardId);
	}
	
	/**
     * Edita o titulo de um dashboard.
     * @throws AccessDeniedException 
	 * @throws DashboardNotFoundException 
     */
	@PatchMapping("/user/{userId}/{dashboardId}/title")
	public DashboardResponseDTO editDashboardTitle(
	        @PathVariable Long userId, 
	        @PathVariable Long dashboardId,
	        @RequestBody DashboardTitleDTO request
			) throws AccessDeniedException, DashboardNotFoundException {
	
        return dashboardService.editTitle(
            dashboardId, 
            userId, 
            request.title()
        );
    }

	/**
     * Acha um dashboard pelo id.
	 * @throws DashboardNotFoundException 
     */
	@GetMapping("/{id}")
	public DashboardResponseDTO findDashboardById(
			@PathVariable Long id
			) throws DashboardNotFoundException {
		
		return dashboardService.findDashboardById(id);
	}
	
	/**
     * Acha todos os dashboards com o id do criador.
     * @throws UserNotFoundException 
     */
	@GetMapping("/user/{userId}")
	public Set<DashboardResponseDTO> findDashboardByUserId(
			@PathVariable Long userId
			) throws UserNotFoundException {
		
		return dashboardService.findAllDashboardByUser(userId);
	}
	
	/**
     * Lista todas as Tags disponiveis no escopo do Dashboard
     * @throws ResourceNotFoundException 
     * @throws AccessDeniedException 
     */
    @GetMapping("/{dashboardId}/tags")
    public ResponseEntity<List<TagResponseDTO>> listTagsByDashboard(
            @AuthenticationPrincipal SecurityUserDetails userDetails,
            @PathVariable Long dashboardId) throws DashboardNotFoundException, AccessDeniedException {
        
        List<TagResponseDTO> tags = dashboardService.listDashboardTags(
            userDetails.getUser().getId(), 
            dashboardId
        );
        return ResponseEntity.ok(tags);
    }
	
	
}
