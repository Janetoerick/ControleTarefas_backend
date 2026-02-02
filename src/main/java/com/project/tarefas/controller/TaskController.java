package com.project.tarefas.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.tarefas.DTO.TaskCreateDTO;
import com.project.tarefas.DTO.TaskPriorityDTO;
import com.project.tarefas.DTO.TaskResponseDTO;
import com.project.tarefas.DTO.TaskStatusDTO;
import com.project.tarefas.config.security.SecurityUserDetails;
import com.project.tarefas.exception.AccessDeniedException;
import com.project.tarefas.exception.DashboardNotFoundException;
import com.project.tarefas.exception.ResourceNotFoundException;
import com.project.tarefas.exception.TagNotFoundException;
import com.project.tarefas.exception.TaskNotFoundException;
import com.project.tarefas.model.User;
import com.project.tarefas.service.TaskService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    @Autowired
    private TaskService taskService;

    /**
     * Criar uma nova tarefa.
     * @throws DashboardNotFoundException 
     * @throws AccessDeniedException 
     * @throws ResourceNotFoundException 
     */
    @PostMapping("/dashboard/{dashboardId}")
    public ResponseEntity<TaskResponseDTO> create(
            @PathVariable Long dashboardId,
            @RequestBody @Valid TaskCreateDTO taskCreateDTO,
            @AuthenticationPrincipal SecurityUserDetails userDetails) throws AccessDeniedException, DashboardNotFoundException, ResourceNotFoundException {
        
        TaskResponseDTO response = taskService.createTask(userDetails.getUser().getId(), dashboardId, taskCreateDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Deletar uma tarefa.
     * @throws TaskNotFoundException 
     * @throws AccessDeniedException 
     * @throws ResourceNotFoundException 
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long taskId,
            @AuthenticationPrincipal SecurityUserDetails userDetails) throws AccessDeniedException, TaskNotFoundException, ResourceNotFoundException {
        taskService.deleteTask(userDetails.getUser().getId(), taskId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Atualizar apenas o Status da tarefa (A_FAZER, EM_ANDAMENTO, CONCLUIDA).
     * @throws TaskNotFoundException 
     * @throws AccessDeniedException 
     * @throws ResourceNotFoundException 
     */
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponseDTO> updateStatus(
            @PathVariable Long taskId,
            @RequestBody TaskStatusDTO statusDTO,
            @AuthenticationPrincipal SecurityUserDetails userDetails) throws AccessDeniedException, TaskNotFoundException, ResourceNotFoundException {
        
        TaskResponseDTO response = taskService.updateTaskStatus(userDetails.getUser().getId(), taskId, statusDTO.status());
        return ResponseEntity.ok(response);
    }

    /**
     * Atualizar apenas a Prioridade da tarefa (BAIXA, MEDIA, ALTA).
     * @throws TaskNotFoundException 
     * @throws AccessDeniedException 
     * @throws ResourceNotFoundException 
     */
    @PatchMapping("/{taskId}/priority")
    public ResponseEntity<TaskResponseDTO> updatePriority(
            @PathVariable Long taskId,
            @RequestBody TaskPriorityDTO taskPriorityDTO,
            @AuthenticationPrincipal SecurityUserDetails userDetails) throws AccessDeniedException, TaskNotFoundException, ResourceNotFoundException {
        
        TaskResponseDTO response = taskService.updateTaskPriority(userDetails.getUser().getId(), taskId, taskPriorityDTO.priority());
        return ResponseEntity.ok(response);
    }

    /**
     * Adiciona a tag na tarefa
     * @throws TaskNotFoundException 
     * @throws AccessDeniedException 
     * @throws TagNotFoundException
     * @throws DashboardNotFoundException 
     * @throws ResourceNotFoundException 
     */
    @PostMapping("/{taskId}/tags/{tagId}")
    public ResponseEntity<TaskResponseDTO> addTag(
            @PathVariable Long taskId,
            @PathVariable Long tagId,
            @AuthenticationPrincipal SecurityUserDetails userDetails) throws AccessDeniedException, TaskNotFoundException, TagNotFoundException, DashboardNotFoundException, ResourceNotFoundException {
        return ResponseEntity.ok(taskService.addTagToTask(userDetails.getUser().getId(), taskId, tagId));
    }

    /**
     * Remove a tag da tarefa
     * @throws TagNotFoundException 
     * @throws TaskNotFoundException 
     * @throws AccessDeniedException 
     * @throws DashboardNotFoundException 
     * @throws ResourceNotFoundException 
     */
    @DeleteMapping("/{taskId}/tags/{tagId}")
    public ResponseEntity<TaskResponseDTO> removeTag(
            @PathVariable Long taskId,
            @PathVariable Long tagId,
            @AuthenticationPrincipal SecurityUserDetails userDetails) throws AccessDeniedException, TaskNotFoundException, TagNotFoundException, DashboardNotFoundException, ResourceNotFoundException {
        
        return ResponseEntity.ok(taskService.removeTagFromTask(userDetails.getUser().getId(), taskId, tagId));
    }

    /**
     * Muda o grupo da tarefa
     * @throws Throwable 
     * @throws AccessDeniedException 
     */
    @PatchMapping("/{taskId}/move/{newGroupId}")
    public ResponseEntity<TaskResponseDTO> moveTask(
            @PathVariable Long taskId,
            @PathVariable Long newGroupId,
            @AuthenticationPrincipal SecurityUserDetails userDetails) throws AccessDeniedException, Throwable {
        return ResponseEntity.ok(taskService.moveTaskToGroup(userDetails.getUser().getId(), taskId, newGroupId));
    }

    /**
     * Lista todas as tarefas de um grupo
     * @throws AccessDeniedException 
     */
    @GetMapping("/group/{taskGroupId}")
    public ResponseEntity<List<TaskResponseDTO>> getByGroup(
            @PathVariable Long taskGroupId,
            @AuthenticationPrincipal SecurityUserDetails userDetails) throws AccessDeniedException {
        return ResponseEntity.ok(taskService.getTasksByGroup(userDetails.getUser().getId(), taskGroupId));
    }

    /**
     * Ver todos os detalhes de uma tarefa
     * @throws AccessDeniedException 
     * @throws TaskNotFoundException 
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> getById(
            @PathVariable Long taskId,
            @AuthenticationPrincipal SecurityUserDetails userDetails) throws AccessDeniedException, TaskNotFoundException {
        return ResponseEntity.ok(taskService.getTaskById(userDetails.getUser().getId(), taskId));
    }
}
