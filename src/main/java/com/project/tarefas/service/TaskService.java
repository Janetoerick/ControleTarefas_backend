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

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskGroupRepository taskGroupRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, TaskGroupRepository taskGroupRepository,
            TagRepository tagRepository, DashboardRepository dashboardRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskGroupRepository = taskGroupRepository;
        this.tagRepository = tagRepository;
        this.dashboardRepository = dashboardRepository;
        this.taskMapper = taskMapper;
    }


    // Método para criar uma Task
    public TaskResponseDTO createTask(Long userId, Long dashboardId, TaskCreateDTO dto) throws DashboardNotFoundException, AccessDeniedException {
        // 1. Validar Dashboard
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
            .orElseThrow(() -> new DashboardNotFoundException("Dashboard não encontrado."));

        // 2. Validar Posse (Security)
        if (!dashboard.getUser().getId().equals(userId)) {
            throw new AccessDeniedException();
        }

        // 3. Validar TaskGroup (A lista onde a tarefa será inserida)
        TaskGroup group = taskGroupRepository.findById(dto.getTaskGroupId())
            .orElseThrow(() -> new EntityNotFoundException("Grupo de tarefas não encontrado."));
        
        // Verificação de segurança extra: o grupo pertence a este dashboard?
        if (!group.getDashboard().getId().equals(dashboardId)) {
            throw new IllegalArgumentException("O grupo de tarefas não pertence a este dashboard.");
        }

        // 4. Instanciar e Preencher a Entidade Task
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDate_init(dto.getDate_init());
        task.setDate_finish(dto.getDate_finish());
        task.setDashboard(dashboard);
        task.setTaskGroup(group);
        
        // Definir valores padrão iniciais (Enums da sua classe Task)
        task.setStatus(StatusTask.PENDENTE); // Exemplo de status inicial
        
        // Converter prioridade de String (DTO) para Enum (Entidade)
        if (dto.getPriority() != null) {
            task.setPriority(Priority.valueOf(dto.getPriority().toUpperCase()));
        }

        // 5. Salvar e Mapear para Resposta
        Task savedTask = taskRepository.save(task);
        return taskMapper.toResponseDTO(savedTask);
    }

    // Método para adicionar uma Tag na Task
    public TaskResponseDTO addTagToTask(Long userId, Long taskId, Long tagId) throws TaskNotFoundException, AccessDeniedException, TagNotFoundException {
        // Busca a tarefa
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException());

        // Validação de Posse: O usuário é dono do dashboard desta tarefa?
        if (!task.getDashboard().getUser().getId().equals(userId)) {
            throw new AccessDeniedException();
        }

        // Busca a tag
        Tag tag = tagRepository.findById(tagId)
            .orElseThrow(() -> new TagNotFoundException());

        // VALIDAÇÃO DE ESCOPO: A tag pertence ao mesmo dashboard da tarefa?
        if (!tag.getDashboard().getId().equals(task.getDashboard().getId())) {
            throw new IllegalArgumentException("A tag selecionada não pertence a este dashboard.");
        }

        // Adiciona e salva
        task.getTags().add(tag);
        return taskMapper.toResponseDTO(taskRepository.save(task));
    }

    // Método para exlucir uma Tag da Task
    public TaskResponseDTO removeTagFromTask(Long userId, Long taskId, Long tagId) throws TaskNotFoundException, TagNotFoundException, AccessDeniedException {
        // 1. Busca a tarefa
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException());

        // 2. Validação de Segurança: O utilizador é dono do dashboard desta tarefa?
        // Reutilizando a lógica de validação de posse
        if (!task.getDashboard().getUser().getId().equals(userId)) {
            throw new AccessDeniedException();
        }

        // 3. Busca a etiqueta
        Tag tag = tagRepository.findById(tagId)
            .orElseThrow(() -> new TagNotFoundException());

        // 4. Remove a etiqueta da coleção da tarefa
        // O Hibernate tratará de remover o registo na tabela intermédia 'task_tag'
        if (task.getTags().contains(tag)) {
            task.getTags().remove(tag);
        } else {
            throw new IllegalArgumentException("Esta etiqueta não está associada a esta tarefa.");
        }

        // 5. Salva a alteração e retorna o DTO mapeado
        return taskMapper.toResponseDTO(taskRepository.save(task));
    }

    // Método para excluir uma Task
    public void deleteTask(Long userId, Long taskId) throws TaskNotFoundException, AccessDeniedException {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException());

        if (!task.getDashboard().getUser().getId().equals(userId)) {
            throw new AccessDeniedException();
        }

        taskRepository.delete(task);
    }

    // Método para atualizar apenas o STATUS
    public TaskResponseDTO updateTaskStatus(Long userId, Long taskId, String statusName) throws TaskNotFoundException, AccessDeniedException {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException());
        
        validateOwner(task, userId);

        try {
            task.setStatus(StatusTask.valueOf(statusName.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + statusName);
        }

        return taskMapper.toResponseDTO(taskRepository.save(task));
    }

    // Método para atualizar apenas a PRIORIDADE
    public TaskResponseDTO updateTaskPriority(Long userId, Long taskId, String priorityName) throws TaskNotFoundException, AccessDeniedException {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException());
        
        validateOwner(task, userId);

        try {
            task.setPriority(Priority.valueOf(priorityName.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Prioridade inválida: " + priorityName);
        }

        return taskMapper.toResponseDTO(taskRepository.save(task));
    }

    // Método para mover uma Task para outro grupo
    public TaskResponseDTO moveTaskToGroup(Long userId, Long taskId, Long newGroupId) throws TaskNotFoundException, AccessDeniedException {
        // 1. Busca a tarefa e valida o dono
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException());
        
        validateOwner(task, userId);

        // 2. Busca o novo grupo (coluna)
        TaskGroup newGroup = taskGroupRepository.findById(newGroupId)
            .orElseThrow(() -> new EntityNotFoundException("Grupo de tarefas não encontrado."));

        // 3. VALIDAÇÃO DE ESCOPO: O novo grupo pertence ao dashboard da tarefa?
        if (!newGroup.getDashboard().getId().equals(task.getDashboard().getId())) {
            throw new IllegalArgumentException("O grupo de destino deve pertencer ao mesmo dashboard da tarefa.");
        }

        // 4. Atualiza a associação
        task.setTaskGroup(newGroup);

        // 5. Salva e retorna o DTO
        return taskMapper.toResponseDTO(taskRepository.save(task));
    }

    // Método para atualizar apenas a PRIORIDADE
    public List<TaskResponseDTO> getTasksByGroup(Long userId, Long taskGroupId) throws AccessDeniedException {
        TaskGroup group = taskGroupRepository.findById(taskGroupId)
            .orElseThrow(() -> new EntityNotFoundException("Grupo de tarefas não encontrado."));

        // Validação de segurança: o grupo pertence a um dashboard do usuário?
        if (!group.getDashboard().getUser().getId().equals(userId)) {
            throw new AccessDeniedException();
        }

        List<Task> tasks = taskRepository.findByTaskGroupId(taskGroupId);
        return tasks.stream()
                    .map(taskMapper::toResponseDTO)
                    .collect(Collectors.toList());
    }

    public TaskResponseDTO getTaskById(Long userId, Long taskId) throws TaskNotFoundException, AccessDeniedException {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException());

        // Validação de segurança
        validateOwner(task, userId);

        return taskMapper.toResponseDTO(task);
    }

    // Validação de segurança: o usuário é o dono do dashboard da tarefa?
    private void validateOwner(Task task, Long userId) throws AccessDeniedException {
        if (!task.getDashboard().getUser().getId().equals(userId)) {
            throw new AccessDeniedException();
        }
    }
}
