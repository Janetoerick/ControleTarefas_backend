package com.project.tarefas.service;

import java.sql.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.tarefas.DTO.TaskCreateDTO;
import com.project.tarefas.DTO.TaskResponseDTO;
import com.project.tarefas.exception.AccessDeniedException;
import com.project.tarefas.exception.DashboardNotFoundException;
import com.project.tarefas.exception.ResourceNotFoundException;
import com.project.tarefas.exception.TagNotFoundException;
import com.project.tarefas.exception.TaskNotFoundException;
import com.project.tarefas.mapper.TaskMapper;
import com.project.tarefas.model.Dashboard;
import com.project.tarefas.model.Tag;
import com.project.tarefas.model.Task;
import com.project.tarefas.model.TaskGroup;
import com.project.tarefas.model.enums.Priority;
import com.project.tarefas.model.enums.StatusTask;
import com.project.tarefas.repository.DashboardRepository;
import com.project.tarefas.repository.TagRepository;
import com.project.tarefas.repository.TaskGroupRepository;
import com.project.tarefas.repository.TaskRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class TaskService {
    
    private TaskRepository taskRepository;
    private TaskGroupRepository taskGroupRepository;
    private TagRepository tagRepository;
    private DashboardRepository dashboardRepository;
    private TaskMapper taskMapper;
    private final ActionService actionService;

    public TaskService(TaskRepository taskRepository, TaskGroupRepository taskGroupRepository,
            TagRepository tagRepository, DashboardRepository dashboardRepository, TaskMapper taskMapper,
            ActionService actionService) {
        this.taskRepository = taskRepository;
        this.taskGroupRepository = taskGroupRepository;
        this.tagRepository = tagRepository;
        this.dashboardRepository = dashboardRepository;
        this.taskMapper = taskMapper;
        this.actionService = actionService;
    }


    // Método para criar uma Task
    public TaskResponseDTO createTask(Long userId, Long dashboardId, TaskCreateDTO dto) {
        // Validar Dashboard
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
            .orElseThrow(() -> new DashboardNotFoundException("Dashboard não encontrado."));

        validateAccess(dashboard, userId);

        // Validar TaskGroup (A lista onde a tarefa será inserida)
        TaskGroup group = taskGroupRepository.findById(dto.taskGroupId())
            .orElseThrow(() -> new EntityNotFoundException("Grupo de tarefas não encontrado."));
        
        // Verificação de segurança extra: o grupo pertence a este dashboard?
        if (!group.getDashboard().getId().equals(dashboardId)) {
            throw new IllegalArgumentException("O grupo de tarefas não pertence a este dashboard.");
        }

        // Instanciar e Preencher a Entidade Task
        Task task = new Task();
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setDate_init(dto.date_init());
        task.setDate_finish(dto.date_finish());
        task.setDashboard(dashboard);
        task.setTaskGroup(group);
        
        // Definir valores padrão iniciais
        task.setStatus(StatusTask.PENDENTE);
        
        // Converter prioridade de String (DTO) para Enum (Entidade)
        if (dto.priority() != null) {
            task.setPriority(Priority.valueOf(dto.priority().toUpperCase()));
        }

        Task savedTask = taskRepository.save(task);

        // Registro no Histórico
        actionService.recordAction(dashboardId, userId, "Criou a tarefa: " + savedTask.getTitle());
        
        return taskMapper.toResponseDTO(savedTask);
    }

    // Método para adicionar uma Tag na Task
    public TaskResponseDTO addTagToTask(Long userId, Long taskId, Long tagId) {
        // Busca a tarefa
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException());

        // Validação de Segurança
        validateAccess(task, userId);

        // Busca a tag
        Tag tag = tagRepository.findById(tagId)
            .orElseThrow(() -> new TagNotFoundException());

        // VALIDAÇÃO DE ESCOPO: A tag pertence ao mesmo dashboard da tarefa
        if (!tag.getDashboard().getId().equals(task.getDashboard().getId())) {
            throw new IllegalArgumentException("A tag selecionada não pertence a este dashboard.");
        }

        // Adiciona e salva
        task.getTags().add(tag);
        Task savedTask = taskRepository.save(task);

        // Registro no Histórico
        actionService.recordAction(savedTask.getDashboard().getId(), userId, 
            "Adicionou a etiqueta '" + tag.getLabel() + "' à tarefa '" + savedTask.getTitle() + "'");

        return taskMapper.toResponseDTO(savedTask);
    }

    // Método para exlucir uma Tag da Task
    public TaskResponseDTO removeTagFromTask(Long userId, Long taskId, Long tagId) {
        // Busca a tarefa
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException());

        // Validação de Segurança
        validateAccess(task, userId);

        // Busca a etiqueta
        Tag tag = tagRepository.findById(tagId)
            .orElseThrow(() -> new TagNotFoundException());

        // Remove a etiqueta da coleção da tarefa
        if (task.getTags().contains(tag)) {
            task.getTags().remove(tag);
        } else {
            throw new IllegalArgumentException("Esta etiqueta não está associada a esta tarefa.");
        }

        Task savedTask = taskRepository.save(task);

        // Registro no Histórico
        actionService.recordAction(savedTask.getDashboard().getId(), userId, 
            "Remove a etiqueta '" + tag.getLabel() + "' à tarefa '" + savedTask.getTitle() + "'");

        return taskMapper.toResponseDTO(taskRepository.save(savedTask));
    }

    // Método para excluir uma Task
    public void deleteTask(Long userId, Long taskId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException());

        // Verifica se o dono do dashboard que esta tentando excluir
        validateOwner(task, userId);

        String taskTitle = task.getTitle();
        Long dashboardId = task.getDashboard().getId();

        taskRepository.delete(task);

        actionService.recordAction(dashboardId, userId, "Excluiu a tarefa: " + taskTitle);
    }

    // Método para atualizar apenas o STATUS
    @Transactional
    public TaskResponseDTO updateTaskStatus(Long userId, Long taskId, String statusName) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException());
        
        validateAccess(task, userId);
        StatusTask oldStatus = task.getStatus();

        try {
            task.setStatus(StatusTask.valueOf(statusName.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + statusName);
        }

        Task updatedTask = taskRepository.save(task);

        // Registro no Histórico
        actionService.recordAction(updatedTask.getDashboard().getId(), userId, 
            "Alterou o status da tarefa '" + updatedTask.getTitle() + "' de " + oldStatus + " para " + statusName.toUpperCase());

        return taskMapper.toResponseDTO(updatedTask);
    }

    // Método para atualizar apenas a PRIORIDADE
    public TaskResponseDTO updateTaskPriority(Long userId, Long taskId, String priorityName) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException());
        
        validateAccess(task, userId);
        Priority oldPriority = task.getPriority();

        try {
            task.setPriority(Priority.valueOf(priorityName.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Prioridade inválida: " + priorityName);
        }

        Task updatedTask = taskRepository.save(task);

        // Registro no Histórico
        actionService.recordAction(updatedTask.getDashboard().getId(), userId, 
            "Alterou a prioridade da tarefa '" + updatedTask.getTitle() + "' de " + oldPriority + " para " + priorityName.toUpperCase());

        return taskMapper.toResponseDTO(updatedTask);
    }

    // Método para mover uma Task para outro grupo
    public TaskResponseDTO moveTaskToGroup(Long userId, Long taskId, Long newGroupId)  {
        // Busca a tarefa e valida o dono
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException());
        
        validateAccess(task, userId);
        String oldGroup = task.getTaskGroup().getTitle();

        // Busca o novo grupo
        TaskGroup newGroup = taskGroupRepository.findById(newGroupId)
            .orElseThrow(() -> new EntityNotFoundException("Grupo de tarefas não encontrado."));

        // VALIDAÇÃO DE ESCOPO: O novo grupo pertence ao dashboard da tarefa
        if (!newGroup.getDashboard().getId().equals(task.getDashboard().getId())) {
            throw new IllegalArgumentException("O grupo de destino deve pertencer ao mesmo dashboard da tarefa.");
        }

        task.setTaskGroup(newGroup);

        Task updatedTask = taskRepository.save(task);

        // Registro no Histórico
        actionService.recordAction(updatedTask.getDashboard().getId(), userId, 
            "Alterou o grupo da tarefa '" + updatedTask.getTitle() + "' de " + oldGroup + " para " + newGroup);

        return taskMapper.toResponseDTO(updatedTask);
    }

    public TaskResponseDTO getTaskById(Long userId, Long taskId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException());

        // Validação de segurança
        validateAccess(task, userId);

        return taskMapper.toResponseDTO(task);
    }

    // Validação de segurança: o usuário é o dono do dashboard da tarefa
    private void validateOwner(Task task, Long userId) {
        if (!task.getDashboard().getUser().getId().equals(userId)) {
            throw new AccessDeniedException();
        }
    }

    private void validateAccess(Dashboard dashboard, Long userId) {
        // Verifica se é o dono
        boolean isOwner = dashboard.getUser().getId().equals(userId);
        
        // Verifica se está no time
        boolean isMember = dashboard.getTeam() != null && dashboard.getTeam().stream()
            .anyMatch(u -> u.getId().equals(userId));

        if (!isOwner && !isMember) {
            throw new AccessDeniedException();
        }
    }

    private void validateAccess(Task task, Long userId) {
        // Verifica se é o dono
        boolean isOwner = task.getDashboard().getUser().getId().equals(userId);
        
        // Verifica se está no time
        boolean isMember = task.getDashboard().getTeam() != null && task.getDashboard().getTeam().stream()
            .anyMatch(u -> u.getId().equals(userId));

        if (!isOwner && !isMember) {
            throw new AccessDeniedException();
        }
    }
}
