package com.project.tarefas.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.tarefas.DTO.TagCreateDTO;
import com.project.tarefas.DTO.TagResponseDTO;
import com.project.tarefas.exception.AccessDeniedException;
import com.project.tarefas.exception.ResourceNotFoundException;
import com.project.tarefas.mapper.TagMapper;
import com.project.tarefas.model.Dashboard;
import com.project.tarefas.model.Tag;
import com.project.tarefas.model.Task;
import com.project.tarefas.repository.DashboardRepository;
import com.project.tarefas.repository.TagRepository;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final DashboardRepository dashboardRepository;
    private final TagMapper tagMapper;

    public TagService(TagRepository tagRepository, 
                      DashboardRepository dashboardRepository, 
                      TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.dashboardRepository = dashboardRepository;
        this.tagMapper = tagMapper;
    }

    // Cria a Tag para poder ser usada no escopo do Dashboard -> Apenas DONO
    @Transactional
    public TagResponseDTO createTag(Long userId, Long dashboardId, TagCreateDTO dto) throws AccessDeniedException, ResourceNotFoundException {
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new ResourceNotFoundException("Dashboard não encontrado"));

        validateStrictOwner(dashboard, userId);

        Tag tag = new Tag(dto.label(), dto.color(), dashboard);
        return tagMapper.toResponseDTO(tagRepository.save(tag));
    }

    // Edita a Tag -> Apenas DONO
    @Transactional
    public TagResponseDTO updateTag(Long userId, Long tagId, TagCreateDTO dto) throws AccessDeniedException, ResourceNotFoundException {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag não encontrada"));

        validateStrictOwner(tag.getDashboard(), userId);

        tag.setLabel(dto.label());
        tag.setColor(dto.color());
        
        return tagMapper.toResponseDTO(tagRepository.save(tag));
    }

    // Deleta uma Tag do Dashboard -> Apenas DONO
    @Transactional
    public void deleteTag(Long userId, Long tagId) throws AccessDeniedException, ResourceNotFoundException {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag não encontrada"));

        validateStrictOwner(tag.getDashboard(), userId);

        for (Task task : tag.getTasks()) {
            task.getTags().remove(tag);
        }

        tagRepository.delete(tag);
    }

    // --- MÉTODOS DE VALIDAÇÃO REUTILIZÁVEIS ---

    // Verifica se é o dono (Restrito)
    private void validateStrictOwner(Dashboard dashboard, Long userId) throws AccessDeniedException {
        if (!dashboard.getUser().getId().equals(userId)) {
            throw new AccessDeniedException();
        }
    }

    // Verifica se pertence ao Dashboard (Dono ou Equipe)
    private void validateDashboardAccess(Dashboard dashboard, Long userId) throws AccessDeniedException {
        boolean isOwner = dashboard.getUser().getId().equals(userId);
        boolean isMember = dashboard.getTeam() != null && 
                           dashboard.getTeam().stream().anyMatch(u -> u.getId().equals(userId));

        if (!isOwner && !isMember) {
            throw new AccessDeniedException();
        }
    }
}
