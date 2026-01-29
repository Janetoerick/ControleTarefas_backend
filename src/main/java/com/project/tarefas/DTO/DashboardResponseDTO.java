package com.project.tarefas.DTO;

import java.util.Set;

public record DashboardResponseDTO(
    Long id,
    String title,
    Set<Long> tasks,
    Set<Long> tags,
    Long user,
    Set<Long> taskgroups,
    Set<Long> team,
    Long historical
) {
    public DashboardResponseDTO(Long id, String title, Long user) {
        this(id, title, null, null, user, null, null, null);
    }
}