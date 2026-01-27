package com.project.tarefas.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.project.tarefas.DTO.TaskGroupResponseDTO;
import com.project.tarefas.model.TaskGroup;

@Mapper(componentModel = "spring")
public interface TaskGroupMapper {

    // Mapeia o ID do objeto Dashboard para o campo Long dashboardId do DTO
    @Mapping(source = "dashboard.id", target = "dashboardId")
    TaskGroupResponseDTO toResponseDTO(TaskGroup taskGroup);
}
