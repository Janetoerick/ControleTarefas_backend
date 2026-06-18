package com.project.tarefas.controller;

import com.project.tarefas.DTO.CommentCreateDTO;
import com.project.tarefas.DTO.CommentResponseDTO;
import com.project.tarefas.config.security.SecurityUserDetails;
import com.project.tarefas.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Cria um comentário para uma tarefa específica
     */
    @PostMapping("/task/{taskId}")
    public ResponseEntity<CommentResponseDTO> create(
            @AuthenticationPrincipal SecurityUserDetails userDetails,
            @PathVariable Long taskId,
            @RequestBody @Valid CommentCreateDTO dto) {
        
        CommentResponseDTO response = commentService.addComment(
                userDetails.getUser().getId(), 
                taskId, 
                dto
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista todos os comentários de uma tarefa
     */
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<CommentResponseDTO>> listByTask(
            @AuthenticationPrincipal SecurityUserDetails userDetails,
            @PathVariable Long taskId) {
        
        List<CommentResponseDTO> comments = commentService.listCommentsByTask(
                userDetails.getUser().getId(), 
                taskId
        );
        return ResponseEntity.ok(comments);
    }

    /**
     * Deleta um comentário
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal SecurityUserDetails userDetails,
            @PathVariable Long commentId) {
        
        commentService.deleteComment(userDetails.getUser().getId(), commentId);
        return ResponseEntity.noContent().build();
    }
}