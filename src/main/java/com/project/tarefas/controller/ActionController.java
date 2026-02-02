package com.project.tarefas.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.tarefas.DTO.ActionResponseDTO;
import com.project.tarefas.config.security.SecurityUserDetails;
import com.project.tarefas.exception.AccessDeniedException;
import com.project.tarefas.exception.ResourceNotFoundException;
import com.project.tarefas.service.ActionService;

@RestController
@RequestMapping("/api/actions")
public class ActionController {

    private final ActionService actionService;

    public ActionController(ActionService actionService) {
        this.actionService = actionService;
    }

    @GetMapping("/dashboard/{dashboardId}")
    public ResponseEntity<Page<ActionResponseDTO>> getHistory(
            @PathVariable Long dashboardId,
            @AuthenticationPrincipal SecurityUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) throws ResourceNotFoundException, AccessDeniedException {
        
        return ResponseEntity.ok(actionService.getHistoryByDashboard(
            dashboardId, 
            userDetails.getUser().getId(), 
            pageable
        ));
    }
}
