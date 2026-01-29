package com.project.tarefas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.tarefas.DTO.TaskCreateDTO;
import com.project.tarefas.DTO.TaskResponseDTO;
import com.project.tarefas.exception.AccessDeniedException;
import com.project.tarefas.mapper.TaskMapper;
import com.project.tarefas.model.Dashboard;
import com.project.tarefas.model.Tag;
import com.project.tarefas.model.Task;
import com.project.tarefas.model.TaskGroup;
import com.project.tarefas.model.User;
import com.project.tarefas.model.enums.Priority;
import com.project.tarefas.model.enums.StatusTask;
import com.project.tarefas.repository.DashboardRepository;
import com.project.tarefas.repository.TagRepository;
import com.project.tarefas.repository.TaskGroupRepository;
import com.project.tarefas.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    
    @Mock private TaskRepository taskRepository;
    @Mock private TaskGroupRepository taskGroupRepository;
    @Mock private TagRepository tagRepository;
    @Mock private DashboardRepository dashboardRepository;
    @Mock private TaskMapper taskMapper;

    @InjectMocks private TaskService taskService;

    private User user;
    private Dashboard dashboard;
    private TaskGroup taskGroup;
    private Task task;
    private Tag tag;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        dashboard = new Dashboard();
        dashboard.setId(10L);
        dashboard.setUser(user);
        dashboard.setTeam(new java.util.HashSet<>());

        taskGroup = new TaskGroup();
        taskGroup.setId(20L);
        taskGroup.setDashboard(dashboard);

        task = new Task();
        task.setId(100L);
        task.setDashboard(dashboard);
        task.setTaskGroup(taskGroup);
        task.setTags(new java.util.HashSet<>());

        tag = new Tag();
        tag.setId(30L);
        tag.setDashboard(dashboard);
    }

    // =================================================================
    // TESTES DE CRIAÇÃO
    // =================================================================

    @Nested
    @DisplayName("Testes de Criação de Tarefa")
    class CreateTaskTests {
        @Test
        void deveCriarTaskComSucesso() throws Exception {
            TaskCreateDTO dto = new TaskCreateDTO("Título", 20L, "ALTA");
            
            when(dashboardRepository.findById(10L)).thenReturn(Optional.of(dashboard));
            when(taskGroupRepository.findById(20L)).thenReturn(Optional.of(taskGroup));
            when(taskRepository.save(any(Task.class))).thenReturn(task);
            when(taskMapper.toResponseDTO(any(Task.class))).thenReturn(new TaskResponseDTO(100L, "Título", "description", 10L, 20L, StatusTask.PENDENTE, Priority.ALTA));

            TaskResponseDTO result = taskService.createTask(1L, 10L, dto);

            assertNotNull(result); // Substitui assertThat(result).isNotNull()
            verify(taskRepository, times(1)).save(any(Task.class));
        }

        @Test
        void deveLancarErroAoCriarEmDashboardAlheio() {
            when(dashboardRepository.findById(10L)).thenReturn(Optional.of(dashboard));
            TaskCreateDTO dto = new TaskCreateDTO();

            // Substitui assertThatThrownBy
            assertThrows(AccessDeniedException.class, () -> {
                taskService.createTask(2L, 10L, dto);
            });
        }
    }

    // =================================================================
    // TESTES DE TAGS
    // =================================================================

    @Nested
    @DisplayName("Testes de Tags")
    class TagTests {
        @Test
        void deveAdicionarTagComSucesso() throws Exception {
            when(taskRepository.findById(100L)).thenReturn(Optional.of(task));
            when(tagRepository.findById(30L)).thenReturn(Optional.of(tag));
            when(taskRepository.save(any(Task.class))).thenReturn(task);

            taskService.addTagToTask(1L, 100L, 30L);

            assertTrue(task.getTags().contains(tag));
            verify(taskRepository).save(task);
        }

        @Test
        void deveRemoverTagComSucesso() throws Exception {
            task.getTags().add(tag);
            when(taskRepository.findById(100L)).thenReturn(Optional.of(task));
            when(tagRepository.findById(30L)).thenReturn(Optional.of(tag));
            when(taskRepository.save(any(Task.class))).thenReturn(task);

            taskService.removeTagFromTask(1L, 100L, 30L);

            assertFalse(task.getTags().contains(tag));
        }
    }

    // =================================================================
    // TESTES DE ATUALIZACOES
    // =================================================================

    @Nested
    @DisplayName("Testes de Atualização")
    class UpdateTests {
        @Test
        void deveAtualizarStatusComSucesso() throws Exception {
            when(taskRepository.findById(100L)).thenReturn(Optional.of(task));
            when(taskRepository.save(any(Task.class))).thenReturn(task);

            taskService.updateTaskStatus(1L, 100L, "PENDENTE");

            assertEquals(StatusTask.PENDENTE, task.getStatus());
        }

        @Test
        void deveMoverTaskDeGrupoComSucesso() throws Exception {
            TaskGroup novoGrupo = new TaskGroup();
            novoGrupo.setId(21L);
            novoGrupo.setDashboard(dashboard);

            when(taskRepository.findById(100L)).thenReturn(Optional.of(task));
            when(taskGroupRepository.findById(21L)).thenReturn(Optional.of(novoGrupo));
            when(taskRepository.save(any(Task.class))).thenReturn(task);

            taskService.moveTaskToGroup(1L, 100L, 21L);

            assertEquals(novoGrupo, task.getTaskGroup());
        }
    }

    // =================================================================
    // TESTES DE BUSCA E DELECAO
    // =================================================================

    @Nested
    @DisplayName("Testes de Busca e Deleção")
    class SearchTests {
        @Test
        void deveBuscarTasksPorGrupoComSucesso() throws Exception {
            when(taskGroupRepository.findById(20L)).thenReturn(Optional.of(taskGroup));
            when(taskRepository.findByTaskGroupId(20L)).thenReturn(List.of(task));

            List<TaskResponseDTO> result = taskService.getTasksByGroup(1L, 20L);

            assertEquals(1, result.size());
        }

        @Test
        void deveDeletarComSucesso() throws Exception {
            when(taskRepository.findById(100L)).thenReturn(Optional.of(task));

            taskService.deleteTask(1L, 100L);

            verify(taskRepository).delete(task);
        }
    }
}
