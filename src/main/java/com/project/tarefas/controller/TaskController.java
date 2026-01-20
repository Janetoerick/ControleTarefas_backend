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
import com.project.tarefas.DTO.TaskResponseDTO;
import com.project.tarefas.exception.AccessDeniedException;
import com.project.tarefas.exception.DashboardNotFoundException;
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
     * Endpoint para criar uma nova tarefa.
     * @throws DashboardNotFoundException 
     * @throws AccessDeniedException 
     */
    @PostMapping("/dashboard/{dashboardId}")
    public ResponseEntity<TaskResponseDTO> create(
            @PathVariable Long dashboardId,
            @RequestBody @Valid TaskCreateDTO taskCreateDTO,
            @AuthenticationPrincipal User user) throws AccessDeniedException, DashboardNotFoundException {
        
        TaskResponseDTO response = taskService.createTask(user.getId(), dashboardId, taskCreateDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Endpoint para deletar uma tarefa.
     * @throws TaskNotFoundException 
     * @throws AccessDeniedException 
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long taskId,
            @AuthenticationPrincipal User user) throws AccessDeniedException, TaskNotFoundException {
        taskService.deleteTask(user.getId(), taskId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Atualizar apenas o Status da tarefa (A_FAZER, EM_ANDAMENTO, CONCLUIDA).
     * @throws TaskNotFoundException 
     * @throws AccessDeniedException 
     */
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponseDTO> updateStatus(
            @PathVariable Long taskId,
            @RequestParam String status,
            @AuthenticationPrincipal User user) throws AccessDeniedException, TaskNotFoundException {
        
        TaskResponseDTO response = taskService.updateTaskStatus(user.getId(), taskId, status);
        return ResponseEntity.ok(response);
    }

    /**
     * Atualizar apenas a Prioridade da tarefa (BAIXA, MEDIA, ALTA).
     * @throws TaskNotFoundException 
     * @throws AccessDeniedException 
     */
    @PatchMapping("/{taskId}/priority")
    public ResponseEntity<TaskResponseDTO> updatePriority(
            @PathVariable Long taskId,
            @RequestParam String priority,
            @AuthenticationPrincipal User user) throws AccessDeniedException, TaskNotFoundException {
        
        TaskResponseDTO response = taskService.updateTaskPriority(user.getId(), taskId, priority);
        return ResponseEntity.ok(response);
    }

    /**
     * Adiciona a tag na tarefa
     * @throws TaskNotFoundException 
     * @throws AccessDeniedException 
     */
    @PostMapping("/{taskId}/tags/{tagId}")
    public ResponseEntity<TaskResponseDTO> addTag(
            @PathVariable Long taskId,
            @PathVariable Long tagId,
            @AuthenticationPrincipal User user) throws AccessDeniedException, TaskNotFoundException, TagNotFoundException {
        return ResponseEntity.ok(taskService.addTagToTask(user.getId(), taskId, tagId));
    }

    /**
     * Remove a tag da tarefa
     * @throws TagNotFoundException 
     * @throws TaskNotFoundException 
     * @throws AccessDeniedException 
     */
    @DeleteMapping("/{taskId}/tags/{tagId}")
    public ResponseEntity<TaskResponseDTO> removeTag(
            @PathVariable Long taskId,
            @PathVariable Long tagId,
            @AuthenticationPrincipal User user) throws AccessDeniedException, TaskNotFoundException, TagNotFoundException {
        // Implementaremos este no Service a seguir, se desejar
        return ResponseEntity.ok(taskService.removeTagFromTask(user.getId(), taskId, tagId));
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
            @AuthenticationPrincipal User user) throws AccessDeniedException, Throwable {
        return ResponseEntity.ok(taskService.moveTaskToGroup(user.getId(), taskId, newGroupId));
    }

    /**
     * Lista todas as tarefas de um grupo
     * @throws Throwable 
     * @throws AccessDeniedException 
     */
    @GetMapping("/group/{taskGroupId}")
    public ResponseEntity<List<TaskResponseDTO>> getByGroup(
            @PathVariable Long taskGroupId,
            @AuthenticationPrincipal User user) throws AccessDeniedException {
        return ResponseEntity.ok(taskService.getTasksByGroup(user.getId(), taskGroupId));
    }

    /**
     * Ver todos os detalhes de uma tarefa
     * @throws AccessDeniedException 
     * @throws TaskNotFoundException 
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> getById(
            @PathVariable Long taskId,
            @AuthenticationPrincipal User user) throws AccessDeniedException, TaskNotFoundException {
        return ResponseEntity.ok(taskService.getTaskById(user.getId(), taskId));
    }
}
