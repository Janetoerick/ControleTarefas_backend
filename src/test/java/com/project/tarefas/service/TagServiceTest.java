package com.project.tarefas.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

import com.project.tarefas.DTO.TagCreateDTO;
import com.project.tarefas.DTO.TagResponseDTO;
import com.project.tarefas.exception.AccessDeniedException;
import com.project.tarefas.exception.ResourceNotFoundException;
import com.project.tarefas.mapper.TagMapper;
import com.project.tarefas.model.Dashboard;
import com.project.tarefas.model.Tag;
import com.project.tarefas.model.Task;
import com.project.tarefas.model.User;
import com.project.tarefas.repository.DashboardRepository;
import com.project.tarefas.repository.TagRepository;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock private TagRepository tagRepository;
    @Mock private DashboardRepository dashboardRepository;
    @Mock private TagMapper tagMapper;

    @InjectMocks private TagService tagService;

    private User owner;
    private User member;
    private Dashboard dashboard;
    private Tag tag;
    private TagCreateDTO tagDTO;

    @BeforeEach
    void setUp() {
        owner = new User(); owner.setId(1L);
        member = new User(); member.setId(2L);

        dashboard = new Dashboard();
        dashboard.setId(10L);
        dashboard.setUser(owner);
        dashboard.setTeam(new HashSet<>(Set.of(member)));

        tag = new Tag("Bug", "#FF0000", dashboard);
        tag.setId(100L);
        tag.setTasks(new HashSet<>()); // Inicializa lista de tarefas da tag

        tagDTO = new TagCreateDTO("Feature", "#00FF00");
    }

    // =================================================================
    // TESTES DE CRIAÇÃO
    // =================================================================

    @Test
    @DisplayName("Deve criar Tag com sucesso quando for o dono")
    void createTag_Success() throws AccessDeniedException, ResourceNotFoundException {
        when(dashboardRepository.findById(10L)).thenReturn(Optional.of(dashboard));
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);
        when(tagMapper.toResponseDTO(any())).thenReturn(new TagResponseDTO(100L, "Bug", "#FF0000"));

        TagResponseDTO result = tagService.createTag(1L, 10L, tagDTO);

        assertNotNull(result);
        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    @DisplayName("Deve impedir criação de Tag por membro da equipe (Apenas Dono)")
    void createTag_Fail_MemberAccess() {
        when(dashboardRepository.findById(10L)).thenReturn(Optional.of(dashboard));

        assertThrows(AccessDeniedException.class, () -> tagService.createTag(2L, 10L, tagDTO));
    }

    // =================================================================
    // TESTES DE EDIÇÃO (UPDATE)
    // =================================================================

    @Test
    @DisplayName("Deve editar Tag com sucesso e refletir novos valores")
    void updateTag_Success() throws AccessDeniedException, ResourceNotFoundException {
        when(tagRepository.findById(100L)).thenReturn(Optional.of(tag));
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);
        when(tagMapper.toResponseDTO(any())).thenReturn(new TagResponseDTO(100L, "Feature", "#00FF00"));

        TagResponseDTO result = tagService.updateTag(1L, 100L, tagDTO);

        assertEquals("Feature", tag.getLabel());
        assertEquals("#00FF00", tag.getColor());
        assertNotNull(result);
    }

    // =================================================================
    // TESTES DE LISTAGEM (ACESSOS)
    // =================================================================

    @Test
    @DisplayName("Deve permitir que membro do time liste as Tags")
    void listTags_Success_ForMember() throws AccessDeniedException, ResourceNotFoundException {
        when(dashboardRepository.findById(10L)).thenReturn(Optional.of(dashboard));
        when(tagRepository.findByDashboardId(10L)).thenReturn(List.of(tag));

        List<TagResponseDTO> result = tagService.listDashboardTags(2L, 10L);

        assertFalse(result.isEmpty());
        verify(tagRepository).findByDashboardId(10L);
    }

    // =================================================================
    // TESTES DE DELEÇÃO E INTEGRIDADE
    // =================================================================

    @Test
    @DisplayName("Deve limpar associações com Tasks antes de deletar a Tag")
    void deleteTag_Success_ClearsAssociations() throws AccessDeniedException, ResourceNotFoundException {
        // Arrange: Criar uma Task e vincular à Tag
        Task task = new Task();
        task.setId(500L);
        task.setTags(new HashSet<>(Set.of(tag)));
        tag.getTasks().add(task);

        when(tagRepository.findById(100L)).thenReturn(Optional.of(tag));

        // Act
        tagService.deleteTag(1L, 100L);

        // Assert
        assertTrue(task.getTags().isEmpty(), "A Tag deveria ter sido removida da Task antes da deleção");
        verify(tagRepository).delete(tag);
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar deletar Tag inexistente")
    void deleteTag_Fail_NotFound() {
        when(tagRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tagService.deleteTag(1L, 999L));
    }

    @Test
    @DisplayName("Não deve permitir que membro do time delete uma Tag")
    void deleteTag_Fail_MemberAccess() {
        when(tagRepository.findById(100L)).thenReturn(Optional.of(tag));

        assertThrows(AccessDeniedException.class, () -> tagService.deleteTag(2L, 100L));
        verify(tagRepository, never()).delete(any());
    }
}