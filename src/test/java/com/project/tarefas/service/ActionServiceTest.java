package com.project.tarefas.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.project.tarefas.DTO.ActionResponseDTO;
import com.project.tarefas.exception.AccessDeniedException;
import com.project.tarefas.exception.ResourceNotFoundException;
import com.project.tarefas.model.Action;
import com.project.tarefas.model.Dashboard;
import com.project.tarefas.model.Historical;
import com.project.tarefas.model.User;
import com.project.tarefas.repository.ActionRepository;
import com.project.tarefas.repository.HistoricalRepository;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ActionServiceTest {

    @Mock private ActionRepository actionRepository;
    @Mock private HistoricalRepository historicalRepository;

    @InjectMocks private ActionService actionService;

    private User owner;
    private User member;
    private User stranger;
    private Dashboard dashboard;
    private Historical historical;
    private Action action;

    @BeforeEach
    void setUp() {
        owner = new User(); owner.setId(1L); owner.setName("Dono");
        member = new User(); member.setId(2L); member.setName("Membro");
        stranger = new User(); stranger.setId(3L);

        dashboard = new Dashboard();
        dashboard.setId(10L);
        dashboard.setUser(owner);
        dashboard.setTeam(Set.of(member));

        historical = new Historical(dashboard);
        historical.setId(100L);

        action = new Action("Teste de log", member, historical);
    }

    @Test
    @DisplayName("Deve gravar uma ação com sucesso")
    void recordAction_Success() {
        when(historicalRepository.findByDashboardId(10L)).thenReturn(Optional.of(historical));

        assertDoesNotThrow(() -> actionService.recordAction(10L, 2L, "Mensagem de log"));

        verify(actionRepository, times(1)).save(any(Action.class));
    }

    @Test
    @DisplayName("Deve falhar ao gravar ação se histórico não existir")
    void recordAction_Fail_NotFound() {
        when(historicalRepository.findByDashboardId(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> actionService.recordAction(10L, 1L, "Log"));
    }

    @Test
    @DisplayName("Deve permitir que o DONO veja o histórico")
    void getHistory_Success_Owner() throws ResourceNotFoundException, AccessDeniedException {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Action> page = new PageImpl<>(List.of(action));

        when(historicalRepository.findByDashboardId(10L)).thenReturn(Optional.of(historical));
        when(actionRepository.findByHistoricalIdOrderByTimestampDesc(100L, pageable)).thenReturn(page);

        Page<ActionResponseDTO> result = actionService.getHistoryByDashboard(10L, 1L, pageable);

        assertFalse(result.isEmpty());
        assertEquals("Membro", result.getContent().get(0).userName());
        verify(actionRepository).findByHistoricalIdOrderByTimestampDesc(100L, pageable);
    }

    @Test
    @DisplayName("Deve permitir que um MEMBRO do time veja o histórico")
    void getHistory_Success_Member() {
        Pageable pageable = PageRequest.of(0, 10);
        when(historicalRepository.findByDashboardId(10L)).thenReturn(Optional.of(historical));
        when(actionRepository.findByHistoricalIdOrderByTimestampDesc(anyLong(), any())).thenReturn(Page.empty());

        assertDoesNotThrow(() -> actionService.getHistoryByDashboard(10L, 2L, pageable));
    }

    @Test
    @DisplayName("Deve negar acesso ao histórico para usuários fora do dashboard")
    void getHistory_Fail_AccessDenied() {
        Pageable pageable = PageRequest.of(0, 10);
        when(historicalRepository.findByDashboardId(10L)).thenReturn(Optional.of(historical));

        assertThrows(AccessDeniedException.class, 
            () -> actionService.getHistoryByDashboard(10L, 3L, pageable));
        
        verify(actionRepository, never()).findByHistoricalIdOrderByTimestampDesc(anyLong(), any());
    }
}