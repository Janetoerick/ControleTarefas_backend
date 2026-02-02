package com.project.tarefas.service;

import com.project.tarefas.DTO.ActionResponseDTO;
import com.project.tarefas.exception.AccessDeniedException;
import com.project.tarefas.exception.ResourceNotFoundException;
import com.project.tarefas.model.Action;
import com.project.tarefas.model.Historical;
import com.project.tarefas.model.User;
import com.project.tarefas.repository.ActionRepository;
import com.project.tarefas.repository.HistoricalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActionService {

    private final ActionRepository actionRepository;
    private final HistoricalRepository historicalRepository;

    public ActionService(ActionRepository actionRepository, HistoricalRepository historicalRepository) {
        this.actionRepository = actionRepository;
        this.historicalRepository = historicalRepository;
    }

    @Transactional
    public void recordAction(Long dashboardId, Long userId, String message) throws ResourceNotFoundException {
        Historical historical = historicalRepository.findByDashboardId(dashboardId)
            .orElseThrow(() -> new ResourceNotFoundException("Histórico não encontrado para este Dashboard"));

        User user = new User();
        user.setId(userId);

        Action action = new Action(message, user, historical);
        actionRepository.save(action);
    }

    @Transactional(readOnly = true)
    public Page<ActionResponseDTO> getHistoryByDashboard(Long dashboardId, Long userId, Pageable pageable) throws ResourceNotFoundException, AccessDeniedException {
        Historical historical = historicalRepository.findByDashboardId(dashboardId)
            .orElseThrow(() -> new ResourceNotFoundException("Histórico não encontrado"));

        boolean isOwner = historical.getDashboard().getUser().getId().equals(userId);
        boolean isMember = historical.getDashboard().getTeam().stream()
                .anyMatch(user -> user.getId().equals(userId));

        if (!isOwner && !isMember) {
            throw new AccessDeniedException();
        }

        return actionRepository.findByHistoricalIdOrderByTimestampDesc(historical.getId(), pageable)
            .map(action -> new ActionResponseDTO(
                action.getId(),
                action.getDescription(),
                action.getTimestamp(),
                action.getUser().getName()
            ));
    }
}