package com.project.tarefas.service;

import com.project.tarefas.DTO.CommentCreateDTO;
import com.project.tarefas.DTO.CommentResponseDTO;
import com.project.tarefas.exception.AccessDeniedException;
import com.project.tarefas.exception.ResourceNotFoundException;
import com.project.tarefas.mapper.CommentMapper;
import com.project.tarefas.model.Comment;
import com.project.tarefas.model.Dashboard;
import com.project.tarefas.model.Task;
import com.project.tarefas.model.User;
import com.project.tarefas.repository.CommentRepository;
import com.project.tarefas.repository.TaskRepository;
import com.project.tarefas.service.ActionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final CommentMapper commentMapper;
    private ActionService actionService;

    public CommentService(CommentRepository commentRepository, TaskRepository taskRepository, CommentMapper commentMapper, ActionService actionService) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.commentMapper = commentMapper;
        this.actionService = actionService;
    }

    @Transactional
    public CommentResponseDTO addComment(Long userId, Long taskId, CommentCreateDTO dto) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));


        validateDashboardAccess(task.getDashboard(), userId);

        User author = new User();
        author.setId(userId);

        Comment comment = new Comment();
        comment.setComment(dto.comment());
        comment.setTask(task);
        comment.setUser(author);

        Comment saved = commentRepository.save(comment);

        // Registo no Histórico
        actionService.recordAction(
            task.getDashboard().getId(), 
            userId, 
            "Adicionou um comentário na tarefa: " + task.getTitle()
        );
        
        return commentMapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDTO> listCommentsByTask(Long userId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));

        validateDashboardAccess(task.getDashboard(), userId);

        // Busca ordenada pelos mais recentes
        return commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId)
                .stream()
                .map(commentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comentário não encontrado"));

        Dashboard dashboard = comment.getTask().getDashboard();

        // Regra: Apenas o autor do comentário ou o dono do Dashboard pode deletar
        boolean isAuthor = comment.getUser().getId().equals(userId);
        boolean isDashboardOwner = dashboard.getUser().getId().equals(userId);

        if (!isAuthor && !isDashboardOwner) {
            throw new AccessDeniedException();
        }

        String taskTitle = comment.getTask().getTitle();
        commentRepository.delete(comment);

        // Registo no Histórico
        actionService.recordAction(
            dashboard.getId(), 
            userId, 
            "Eliminou um comentário da tarefa: " + taskTitle
        );
    }

    // --- Métodos Auxiliares ---

    private void validateDashboardAccess(Dashboard dashboard, Long userId) {
        boolean isOwner = dashboard.getUser().getId().equals(userId);
        boolean isMember = dashboard.getTeam() != null && 
                           dashboard.getTeam().stream().anyMatch(u -> u.getId().equals(userId));

        if (!isOwner && !isMember) {
            throw new AccessDeniedException();
        }
    }
}