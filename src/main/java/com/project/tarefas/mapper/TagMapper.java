package com.project.tarefas.mapper;

import com.project.tarefas.DTO.TagResponseDTO;
import com.project.tarefas.model.Tag;
import org.springframework.stereotype.Component;

@Component
public class TagMapper {

    public TagResponseDTO toResponseDTO(Tag tag) {
        return new TagResponseDTO(
            tag.getId(),
            tag.getLabel(),
            tag.getColor()
        );
    }
}