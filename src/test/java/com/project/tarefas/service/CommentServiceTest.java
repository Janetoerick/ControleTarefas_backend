package com.project.tarefas.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock private CommentRepository commentRepository;
    @Mock private TaskRepository taskRepository;
    @Mock private CommentMapper commentMapper;

    @Mock private ActionService actionService;
    @InjectMocks private CommentService commentService;

    private User owner;
    private User member;
    private User stranger;
    private Dashboard dashboard;
    private Task task;
    private Comment comment;

    @BeforeEach
    void setUp() {
        owner = new User(); owner.setId(1L); owner.setName("Dono");
        member = new User(); member.setId(2L); member.setName("Membro");
        stranger = new User(); stranger.setId(3L);

        dashboard = new Dashboard();
        dashboard.setId(10L);
        dashboard.setUser(owner);
        dashboard.setTeam(new HashSet<>(Set.of(member)));

        task = new Task();
        task.setId(100L);
        task.setDashboard(dashboard);

        comment = new Comment("Conteúdo Original", member, task);
        comment.setId(500L);
    }

    @Test
    @DisplayName("Deve adicionar comentário com sucesso se o usuário pertence ao time")
    void addComment_Success() throws ResourceNotFoundException, AccessDeniedException {
        CommentCreateDTO dto = new CommentCreateDTO("Novo comentário");
        when(taskRepository.findById(100L)).thenReturn(Optional.of(task));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toResponseDTO(any())).thenReturn(new CommentResponseDTO(500L, "Novo comentário", "Membro", LocalDateTime.now()));

        CommentResponseDTO result = commentService.addComment(2L, 100L, dto);

        assertNotNull(result);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("Deve impedir comentário de usuário que não pertence ao dashboard")
    void addComment_Fail_AccessDenied() {
        CommentCreateDTO dto = new CommentCreateDTO("Tenta comentar");
        when(taskRepository.findById(100L)).thenReturn(Optional.of(task));

        assertThrows(AccessDeniedException.class, () -> commentService.addComment(3L, 100L, dto));
        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve listar comentários apenas para quem tem acesso ao dashboard")
    void listComments_Success() throws ResourceNotFoundException, AccessDeniedException {
        when(taskRepository.findById(100L)).thenReturn(Optional.of(task));
        when(commentRepository.findByTaskIdOrderByCreatedAtDesc(100L)).thenReturn(List.of(comment));

        List<CommentResponseDTO> result = commentService.listCommentsByTask(1L, 100L);

        assertFalse(result.isEmpty());
        verify(commentRepository).findByTaskIdOrderByCreatedAtDesc(100L);
    }

    @Test
    @DisplayName("Deve permitir que o DONO do dashboard delete qualquer comentário")
    void deleteComment_ByDashboardOwner() {
        when(commentRepository.findById(500L)).thenReturn(Optional.of(comment));

        // Usuário 1L é o dono do dashboard
        assertDoesNotThrow(() -> commentService.deleteComment(1L, 500L));
        verify(commentRepository).delete(comment);
    }

    @Test
    @DisplayName("Deve permitir que o AUTOR delete seu próprio comentário")
    void deleteComment_ByAuthor() {
        when(commentRepository.findById(500L)).thenReturn(Optional.of(comment));

        // Usuário 2L é o autor do comentário (membro)
        assertDoesNotThrow(() -> commentService.deleteComment(2L, 500L));
        verify(commentRepository).delete(comment);
    }

    @Test
    @DisplayName("Deve impedir que outro membro delete comentário alheio")
    void deleteComment_Fail_Forbidden() {
        User anotherMember = new User(); anotherMember.setId(4L);
        comment.setUser(anotherMember); // Comentário é de outro usuário
        
        when(commentRepository.findById(500L)).thenReturn(Optional.of(comment));

        // Membro (2L) tentando deletar comentário do OutroMembro (4L)
        assertThrows(AccessDeniedException.class, () -> commentService.deleteComment(2L, 500L));
        verify(commentRepository, never()).delete(any());
    }
}