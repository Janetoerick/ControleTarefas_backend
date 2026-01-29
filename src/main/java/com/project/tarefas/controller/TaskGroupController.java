package com.project.tarefas.controller;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.project.tarefas.DTO.TaskGroupCreateDTO;
import com.project.tarefas.DTO.TaskGroupResponseDTO;
import com.project.tarefas.config.security.SecurityUserDetails;
import com.project.tarefas.exception.AccessDeniedException;
import com.project.tarefas.exception.DashboardNotFoundException;
import com.project.tarefas.service.TaskGroupService;


@RestController
@RequestMapping("/api/groups")
public class TaskGroupController {

    private final TaskGroupService taskGroupService;

    public TaskGroupController(TaskGroupService taskGroupService) {
        this.taskGroupService = taskGroupService;
    }

    /**
     * Cria uma nova coluna no Dashboard.
     * Apenas o PROPRIETÁRIO do dashboard pode realizar esta ação.
     * @throws AccessDeniedException 
     * @throws DashboardNotFoundException
     */
    @PostMapping("/dashboard/{dashboardId}")
    public ResponseEntity<TaskGroupResponseDTO> create(
            @PathVariable Long dashboardId,
            @RequestBody @Valid TaskGroupCreateDTO dto,
            @AuthenticationPrincipal SecurityUserDetails userDetails) throws AccessDeniedException, DashboardNotFoundException {
        
        TaskGroupResponseDTO response = taskGroupService.create(userDetails.getUser().getId(), dashboardId, dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Lista todas as colunas de um Dashboard.
     * Tanto o PROPRIETÁRIO quanto os membros do TIME podem visualizar.
     * @throws AccessDeniedException 
     * @throws DashboardNotFoundException 
     */
    @GetMapping("/dashboard/{dashboardId}")
    public ResponseEntity<List<TaskGroupResponseDTO>> list(
            @PathVariable Long dashboardId,
            @AuthenticationPrincipal SecurityUserDetails userDetails) throws AccessDeniedException, DashboardNotFoundException {
        
        List<TaskGroupResponseDTO> response = taskGroupService.listByDashboard(userDetails.getUser().getId(), dashboardId);
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza o título de uma coluna.
     * Apenas o PROPRIETÁRIO pode renomear colunas.
     */
    @PutMapping("/{taskGroupId}")
    public ResponseEntity<TaskGroupResponseDTO> update(
            @PathVariable Long taskGroupId,
            @RequestBody @Valid TaskGroupCreateDTO dto,
            @AuthenticationPrincipal SecurityUserDetails userDetails) throws AccessDeniedException {
        
        TaskGroupResponseDTO response = taskGroupService.update(userDetails.getUser().getId(), taskGroupId, dto.title());
        return ResponseEntity.ok(response);
    }

    /**
     * Elimina uma coluna.
     * Apenas o PROPRIETÁRIO pode apagar colunas.
     */
    @DeleteMapping("/{taskGroupId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long taskGroupId,
            @AuthenticationPrincipal SecurityUserDetails userDetails) throws AccessDeniedException {
        
        taskGroupService.delete(userDetails.getUser().getId(), taskGroupId);
        return ResponseEntity.noContent().build();
    }
}
