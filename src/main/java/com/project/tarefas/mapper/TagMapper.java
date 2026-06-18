package com.project.tarefas.mapper;

import com.project.tarefas.DTO.TagResponseDTO;
import com.project.tarefas.model.Tag;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagMapper {

    TagResponseDTO toResponseDTO(Tag tag);
}