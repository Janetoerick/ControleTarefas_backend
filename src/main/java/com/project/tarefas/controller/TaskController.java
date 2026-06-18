package com.project.tarefas.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.project.tarefas.DTO.TaskCreateDTO;
import com.project.tarefas.DTO.TaskPriorityDTO;
import com.project.tarefas.DTO.TaskResponseDTO;
import com.project.tarefas.DTO.TaskStatusDTO;
import com.project.tarefas.config.security.SecurityUserDetails;
import com.project.tarefas.service.TaskService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    @Autowired
    private TaskService taskService;

    /**
     * Criar uma nova tarefa.
     */
    @PostMapping("/dashboard/{dashboardId}")
    public ResponseEntity<TaskResponseDTO> create(
            @PathVariable Long dashboardId,
            @RequestBody @Valid TaskCreateDTO taskCreateDTO,
            @AuthenticationPrincipal SecurityUserDetails userDetails) {
        
        TaskResponseDTO response = taskService.createTask(userDetails.getUser().getId(), dashboardId, taskCreateDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Deletar uma tarefa.
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long taskId,
            @AuthenticationPrincipal SecurityUserDetails userDetails) {
        taskService.deleteTask(userDetails.getUser().getId(), taskId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Atualizar apenas o Status da tarefa (A_FAZER, EM_ANDAMENTO, CONCLUIDA).
     */
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponseDTO> updateStatus(
            @PathVariable Long taskId,
            @RequestBody TaskStatusDTO statusDTO,
            @AuthenticationPrincipal SecurityUserDetails userDetails) {
        
        TaskResponseDTO response = taskService.updateTaskStatus(userDetails.getUser().getId(), taskId, statusDTO.status());
        return ResponseEntity.ok(response);
    }

    /**
     * Atualizar apenas a Prioridade da tarefa (BAIXA, MEDIA, ALTA).
     */
    @PatchMapping("/{taskId}/priority")
    public ResponseEntity<TaskResponseDTO> updatePriority(
            @PathVariable Long taskId,
            @RequestBody TaskPriorityDTO taskPriorityDTO,
            @AuthenticationPrincipal SecurityUserDetails userDetails) {
        
        TaskResponseDTO response = taskService.updateTaskPriority(userDetails.getUser().getId(), taskId, taskPriorityDTO.priority());
        return ResponseEntity.ok(response);
    }

    /**
     * Adiciona a tag na tarefa
     */
    @PostMapping("/{taskId}/tags/{tagId}")
    public ResponseEntity<TaskResponseDTO> addTag(
            @PathVariable Long taskId,
            @PathVariable Long tagId,
            @AuthenticationPrincipal SecurityUserDetails userDetails) {
        return ResponseEntity.ok(taskService.addTagToTask(userDetails.getUser().getId(), taskId, tagId));
    }

    /**
     * Remove a tag da tarefa
     */
    @DeleteMapping("/{taskId}/tags/{tagId}")
    public ResponseEntity<TaskResponseDTO> removeTag(
            @PathVariable Long taskId,
            @PathVariable Long tagId,
            @AuthenticationPrincipal SecurityUserDetails userDetails) {
        
        return ResponseEntity.ok(taskService.removeTagFromTask(userDetails.getUser().getId(), taskId, tagId));
    }

    /**
     * Muda o grupo da tarefa
     */
    @PatchMapping("/{taskId}/move/{newGroupId}")
    public ResponseEntity<TaskResponseDTO> moveTask(
            @PathVariable Long taskId,
            @PathVariable Long newGroupId,
            @AuthenticationPrincipal SecurityUserDetails userDetails) {
        return ResponseEntity.ok(taskService.moveTaskToGroup(userDetails.getUser().getId(), taskId, newGroupId));
    }

    /**
     * Ver todos os detalhes de uma tarefa
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> getById(
            @PathVariable Long taskId,
            @AuthenticationPrincipal SecurityUserDetails userDetails) {
        return ResponseEntity.ok(taskService.getTaskById(userDetails.getUser().getId(), taskId));
    }
}
