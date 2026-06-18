package com.project.tarefas.service;

import com.project.tarefas.exception.ResourceNotFoundException;
import com.project.tarefas.model.Dashboard;
import com.project.tarefas.model.Historical;
import com.project.tarefas.repository.HistoricalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistoricalService {

    private final HistoricalRepository historicalRepository;

    public HistoricalService(HistoricalRepository historicalRepository) {
        this.historicalRepository = historicalRepository;
    }

    // Chamado pelo DashboardService no momento do cadastro
    @Transactional
    public void createForDashboard(Dashboard dashboard) {
        Historical historical = new Historical(dashboard);
        historicalRepository.save(historical);
    }

    // Método de mostragem/recuperação
    @Transactional(readOnly = true)
    public Historical findByDashboard(Long dashboardId) {
        return historicalRepository.findByDashboardId(dashboardId)
            .orElseThrow(() -> new ResourceNotFoundException("Histórico não encontrado"));
    }
}