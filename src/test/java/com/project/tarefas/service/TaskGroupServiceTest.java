package com.project.tarefas.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.Collections;
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

import com.project.tarefas.DTO.TaskGroupCreateDTO;
import com.project.tarefas.DTO.TaskGroupResponseDTO;
import com.project.tarefas.exception.AccessDeniedException;
import com.project.tarefas.exception.DashboardNotFoundException;
import com.project.tarefas.mapper.TaskGroupMapper;
import com.project.tarefas.model.Dashboard;
import com.project.tarefas.model.TaskGroup;
import com.project.tarefas.model.User;
import com.project.tarefas.repository.DashboardRepository;
import com.project.tarefas.repository.TaskGroupRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class TaskGroupServiceTest {

    @Mock private TaskGroupRepository taskGroupRepository;
    @Mock private DashboardRepository dashboardRepository;
    @Mock private TaskGroupMapper taskGroupMapper;

    @InjectMocks private TaskGroupService taskGroupService;

    private User owner;
    private User teamMember;
    private User intruder;
    private Dashboard dashboard;
    private TaskGroup taskGroup;
    private TaskGroupCreateDTO createDTO;

    @BeforeEach
    void setUp() {
        owner = new User(); owner.setId(1L);
        teamMember = new User(); teamMember.setId(2L);
        intruder = new User(); intruder.setId(3L);

        dashboard = new Dashboard();
        dashboard.setId(10L);
        dashboard.setUser(owner);
        dashboard.setTeam(Set.of(teamMember)); // Adiciona membro ao time

        taskGroup = new TaskGroup("To Do", dashboard);
        taskGroup.setId(100L);

        createDTO = new TaskGroupCreateDTO("To do");
    }

    // =================================================================
    // TESTES DE CRIAÇÃO
    // =================================================================

    @Test
    @DisplayName("Deve criar TaskGroup quando o usuário for o dono do dashboard")
    void create_Success() throws Exception {
        when(dashboardRepository.findById(10L)).thenReturn(Optional.of(dashboard));
        when(taskGroupRepository.save(any(TaskGroup.class))).thenReturn(taskGroup);
        when(taskGroupMapper.toResponseDTO(any())).thenReturn(new TaskGroupResponseDTO());

        assertNotNull(taskGroupService.create(1L, 10L, createDTO));
    }

    @Test
    @DisplayName("Deve lançar erro ao criar TaskGroup quando usuário for apenas membro do time")
    void create_FailMember() {
        when(dashboardRepository.findById(10L)).thenReturn(Optional.of(dashboard));
        // O ID 2L (teamMember) não é o dono (1L)
        assertThrows(AccessDeniedException.class, () -> taskGroupService.create(2L, 10L, createDTO));
    }

    @Test
    @DisplayName("Deve lançar DashboardNotFoundException se ID do dashboard for inválido")
    void create_DashboardNotFound() {
        when(dashboardRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(DashboardNotFoundException.class, () -> taskGroupService.create(1L, 99L, createDTO));
    }

    // =================================================================
    // TESTES DE LISTAGEM
    // =================================================================

    @Test
    @DisplayName("Deve listar grupos para o dono do dashboard")
    void list_SuccessOwner() throws Exception {
        when(dashboardRepository.findById(10L)).thenReturn(Optional.of(dashboard));
        when(taskGroupRepository.findByDashboardId(10L)).thenReturn(List.of(taskGroup));

        List<TaskGroupResponseDTO> result = taskGroupService.listByDashboard(1L, 10L);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Deve permitir que membros do time listem os grupos")
    void list_SuccessTeamMember() throws Exception {
        when(dashboardRepository.findById(10L)).thenReturn(Optional.of(dashboard));
        when(taskGroupRepository.findByDashboardId(10L)).thenReturn(List.of(taskGroup));

        // Usuário 2L é membro do time
        assertDoesNotThrow(() -> taskGroupService.listByDashboard(2L, 10L));
    }

    @Test
    @DisplayName("Deve negar listagem para usuário intruso (não é dono nem time)")
    void list_FailIntruder() {
        when(dashboardRepository.findById(10L)).thenReturn(Optional.of(dashboard));
        assertThrows(AccessDeniedException.class, () -> taskGroupService.listByDashboard(3L, 10L));
    }

    // =================================================================
    // TESTES DE ATUALIZACAO
    // =================================================================

    @Test
    @DisplayName("Deve atualizar nome do grupo com sucesso")
    void update_Success() throws Exception {
        when(taskGroupRepository.findById(100L)).thenReturn(Optional.of(taskGroup));
        when(taskGroupRepository.save(any())).thenReturn(taskGroup);
        
        taskGroupService.update(1L, 100L, "Doing");
        
        verify(taskGroupRepository).save(any());
        assertEquals("Doing", taskGroup.getTitle());
    }

    // =================================================================
    // TESTES DE DELECAO
    // =================================================================

    @Test
    @DisplayName("Deve deletar grupo se for o dono")
    void delete_Success() throws Exception {
        when(taskGroupRepository.findById(100L)).thenReturn(Optional.of(taskGroup));
        
        taskGroupService.delete(1L, 100L);
        
        verify(taskGroupRepository).delete(taskGroup);
    }

    @Test
    @DisplayName("Deve impedir deleção se o usuário for intruso")
    void delete_FailIntruder() {
        when(taskGroupRepository.findById(100L)).thenReturn(Optional.of(taskGroup));
        
        assertThrows(AccessDeniedException.class, () -> taskGroupService.delete(3L, 100L));
        verify(taskGroupRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException se grupo não existir")
    void delete_NotFound() {
        when(taskGroupRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> taskGroupService.delete(1L, 999L));
    }
}
