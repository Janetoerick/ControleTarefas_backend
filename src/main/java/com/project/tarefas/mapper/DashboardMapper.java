package com.project.tarefas.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;

import com.project.tarefas.DTO.DashboardResponseDTO;
import com.project.tarefas.model.Dashboard;
import com.project.tarefas.model.Historical;
import com.project.tarefas.model.User;

@Mapper(componentModel = "spring")
public interface DashboardMapper {

	// Ensina o MapStruct a mapear um User para um Long (o ID)
	default Long map(User user) {
        return user != null ? user.getId() : null;
    }
    
    // Ensina o MapStruct a mapear o Set<User> para Set<Long> (para a equipe)
    default Set<Long> mapUsersToIds(Set<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream()
            .map(User::getId)
            .collect(Collectors.toSet());
    }
     
    default Long map(Historical historical) {
        if (historical == null) {
            return null;
        }
        // O tipo de retorno deve ser o mesmo que o tipo do ID na entidade Historical
        return historical.getId(); 
    }
	
	DashboardResponseDTO toResponseDTO(Dashboard dashboard);
	
	Set<DashboardResponseDTO> toResponseAllDTO(Set<Dashboard> dashboards);
}
