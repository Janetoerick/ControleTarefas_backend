package com.project.tarefas.mapper;

import org.mapstruct.Mapper;

import com.project.tarefas.DTO.DashboardResponseDTO;
import com.project.tarefas.model.Dashboard;

@Mapper(componentModel = "spring")
public interface DashboardMapper {

	DashboardResponseDTO toResponseDTO(Dashboard dashboard);
}
