package com.project.tarefas.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.project.tarefas.DTO.UserResponseDTO;
import com.project.tarefas.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Converte User para UserResponseDTO
    UserResponseDTO toResponseDTO(User user);
    
    // Converte uma lista de User para uma lista de UserResponseDTO
    List<UserResponseDTO> toResponseDTOList(List<User> users);
}