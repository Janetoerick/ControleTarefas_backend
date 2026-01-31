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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final CommentMapper commentMapper;

    public CommentService(CommentRepository commentRepository, TaskRepository taskRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.commentMapper = commentMapper;
    }

    @Transactional
    public CommentResponseDTO addComment(Long userId, Long taskId, CommentCreateDTO dto) throws ResourceNotFoundException, AccessDeniedException {

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
        
        return commentMapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDTO> listCommentsByTask(Long userId, Long taskId) throws ResourceNotFoundException, AccessDeniedException {
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
    public void deleteComment(Long userId, Long commentId) throws AccessDeniedException, ResourceNotFoundException {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comentário não encontrado"));

        // Regra: Apenas o autor do comentário ou o dono do Dashboard pode deletar
        boolean isAuthor = comment.getUser().getId().equals(userId);
        boolean isDashboardOwner = comment.getTask().getDashboard().getUser().getId().equals(userId);

        if (!isAuthor && !isDashboardOwner) {
            throw new AccessDeniedException();
        }

        commentRepository.delete(comment);
    }

    // --- Métodos Auxiliares ---

    private void validateDashboardAccess(Dashboard dashboard, Long userId) throws AccessDeniedException {
        boolean isOwner = dashboard.getUser().getId().equals(userId);
        boolean isMember = dashboard.getTeam() != null && 
                           dashboard.getTeam().stream().anyMatch(u -> u.getId().equals(userId));

        if (!isOwner && !isMember) {
            throw new AccessDeniedException();
        }
    }
}