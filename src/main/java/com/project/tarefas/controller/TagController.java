package com.project.tarefas.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.tarefas.DTO.TagCreateDTO;
import com.project.tarefas.DTO.TagResponseDTO;
import com.project.tarefas.config.security.SecurityUserDetails;
import com.project.tarefas.service.TagService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * Cria uma Tag no escopo do Dashboard
     */
    @PostMapping("/dashboard/{dashboardId}")
    public ResponseEntity<TagResponseDTO> create(
            @AuthenticationPrincipal SecurityUserDetails userDetails,
            @PathVariable Long dashboardId,
            @RequestBody @Valid TagCreateDTO dto) {
        
        TagResponseDTO response = tagService.createTag(
            userDetails.getUser().getId(), 
            dashboardId, 
            dto
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Atualiza as inforamções de uma Tag
     */
    @PutMapping("/{tagId}")
    public ResponseEntity<TagResponseDTO> update(
            @AuthenticationPrincipal SecurityUserDetails userDetails,
            @PathVariable Long tagId,
            @RequestBody @Valid TagCreateDTO dto) {
        
        TagResponseDTO response = tagService.updateTag(
            userDetails.getUser().getId(), 
            tagId, 
            dto
        );
        return ResponseEntity.ok(response);
    }


    /**
     * Deleta uma Tag do escopo do Dashboard
     */
    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal SecurityUserDetails userDetails,
            @PathVariable Long tagId) {
        
        tagService.deleteTag(userDetails.getUser().getId(), tagId);
        return ResponseEntity.noContent().build();
    }
}