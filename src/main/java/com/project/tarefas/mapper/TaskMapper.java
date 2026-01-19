package com.project.tarefas.mapper;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.project.tarefas.DTO.TaskResponseDTO;
import com.project.tarefas.model.Tag;
import com.project.tarefas.model.Task;
import com.project.tarefas.model.enums.Priority;
import com.project.tarefas.model.enums.StatusTask;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    
    @Mapping(source = "dashboard.id", target = "dashboardId")
    @Mapping(source = "taskGroup.id", target = "taskGroupId")
    @Mapping(target = "tags", expression = "java(mapTagsToIds(task.getTags()))")
    TaskResponseDTO toResponseDTO(Task task);

    // Converte o Set<Tag> da entidade para Set<Long> no DTO
    default Set<Long> mapTagsToIds(Set<Tag> tags) {
        if (tags == null) return Collections.emptySet();
        return tags.stream()
                   .map(Tag::getId)
                   .collect(Collectors.toSet());
    }

    // Conversores de Enum para String para o ResponseDTO
    default String map(StatusTask status) {
        return status != null ? status.name() : null;
    }

    default String map(Priority priority) {
        return priority != null ? priority.name() : null;
    }
}
