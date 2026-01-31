package com.project.tarefas.mapper;

import com.project.tarefas.DTO.CommentResponseDTO;
import com.project.tarefas.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "content", source = "comment")
    @Mapping(target = "authorName", source = "user.name")
    CommentResponseDTO toResponseDTO(Comment comment);
}