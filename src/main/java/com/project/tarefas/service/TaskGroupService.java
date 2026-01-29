package com.project.tarefas.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.tarefas.DTO.TaskGroupCreateDTO;
import com.project.tarefas.DTO.TaskGroupResponseDTO;
import com.project.tarefas.exception.AccessDeniedException;
import com.project.tarefas.exception.DashboardNotFoundException;
import com.project.tarefas.mapper.TaskGroupMapper;
import com.project.tarefas.model.Dashboard;
import com.project.tarefas.model.TaskGroup;
import com.project.tarefas.repository.DashboardRepository;
import com.project.tarefas.repository.TaskGroupRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TaskGroupService {
    
    private final TaskGroupRepository taskGroupRepository;
    private final DashboardRepository dashboardRepository;
    private final TaskGroupMapper taskGroupMapper;

    public TaskGroupService(TaskGroupRepository taskGroupRepository, 
                            DashboardRepository dashboardRepository, 
                            TaskGroupMapper taskGroupMapper) {
        this.taskGroupRepository = taskGroupRepository;
        this.dashboardRepository = dashboardRepository;
        this.taskGroupMapper = taskGroupMapper;
    }

    // Cria uma nova coluna. Apenas o DONO do dashboard pode fazer isso.
    public TaskGroupResponseDTO create(Long userId, Long dashboardId, TaskGroupCreateDTO dto) throws AccessDeniedException, DashboardNotFoundException {
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new DashboardNotFoundException("Dashboard não encontrado."));

        // Validação restrita ao dono
        validateStrictOwner(dashboard, userId);

        TaskGroup group = new TaskGroup();
        group.setTitle(dto.title());
        group.setDashboard(dashboard);

        return taskGroupMapper.toResponseDTO(taskGroupRepository.save(group));
    }


    // Lista grupos. Aqui permitimos que o TIME veja, caso contrário não conseguiriam ver as tarefas.
    public List<TaskGroupResponseDTO> listByDashboard(Long userId, Long dashboardId) throws AccessDeniedException, DashboardNotFoundException {
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new DashboardNotFoundException("Dashboard não encontrado."));

        // Para listar, usamos a regra do TaskService: Dono OU Time
        validateAccessForView(dashboard, userId);

        return taskGroupRepository.findByDashboardId(dashboardId).stream()
                .map(taskGroupMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Atualiza o nome da coluna. Apenas o DONO.
    public TaskGroupResponseDTO update(Long userId, Long taskGroupId, String newName) throws AccessDeniedException {
        TaskGroup group = taskGroupRepository.findById(taskGroupId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado."));

        validateStrictOwner(group.getDashboard(), userId);

        group.setTitle(newName);
        return taskGroupMapper.toResponseDTO(taskGroupRepository.save(group));
    }

    // Deleta uma coluna. Apenas o DONO.
    public void delete(Long userId, Long taskGroupId) throws AccessDeniedException {
        TaskGroup group = taskGroupRepository.findById(taskGroupId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado."));

        validateStrictOwner(group.getDashboard(), userId);

        taskGroupRepository.delete(group);
    }

    // --- VALIDAÇÕES DE SEGURANÇA ---

    private void validateStrictOwner(Dashboard dashboard, Long userId) throws AccessDeniedException {
        if (!dashboard.getUser().getId().equals(userId)) {
            throw new AccessDeniedException();
        }
    }

    private void validateAccessForView(Dashboard dashboard, Long userId) throws AccessDeniedException {
        boolean isOwner = dashboard.getUser().getId().equals(userId);
        
        // Proteção contra NullPointerException
        boolean isMember = dashboard.getTeam() != null && dashboard.getTeam().stream()
                .anyMatch(u -> u.getId().equals(userId));

        if (!isOwner && !isMember) {
            throw new AccessDeniedException();
        }
    }
}
