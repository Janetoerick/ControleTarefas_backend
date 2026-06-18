package com.project.tarefas.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.tarefas.DTO.DashboardResponseDTO;
import com.project.tarefas.DTO.DashboardTitleDTO;
import com.project.tarefas.DTO.TagResponseDTO;
import com.project.tarefas.exception.AccessDeniedException;
import com.project.tarefas.exception.DashboardNotFoundException;
import com.project.tarefas.exception.ResourceNotFoundException;
import com.project.tarefas.exception.UserNotFoundException;
import com.project.tarefas.mapper.DashboardMapper;
import com.project.tarefas.model.Dashboard;
import com.project.tarefas.model.User;
import com.project.tarefas.repository.DashboardRepository;
import com.project.tarefas.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {

	@Mock
    private DashboardRepository dashboardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DashboardMapper dashboardMapper;

    @InjectMocks
    private DashboardService dashboardService;

    // Dados de Teste
    private final Long VALID_USER_ID = 1L;
    private final Long OTHER_USER_ID = 2L;
    private final Long VALID_DASHBOARD_ID = 100L;
    private final String INITIAL_TITLE = "Dashboard Inicial";
    private final String NEW_TITLE = "Novo Título Editado";

    private User validUser;
    private User otherUser;
    private Dashboard validDashboard;
    private DashboardResponseDTO validDashboardDTO;

    @BeforeEach
    void setUp() {
        // Inicializa o Usuário Válido (Dono do Dashboard)
        validUser = new User(VALID_USER_ID, "Owner", "owner@email.com", "hash1", "ROLE_USER");
        
        // Inicializa Outro Usuário
        otherUser = new User(OTHER_USER_ID, "Guest", "guest@email.com", "hash2", "ROLE_USER");

        // Inicializa o Dashboard
        validDashboard = new Dashboard(VALID_DASHBOARD_ID, INITIAL_TITLE, validUser);

        // Inicializa o DTO de Resposta
        validDashboardDTO = new DashboardResponseDTO(VALID_DASHBOARD_ID, INITIAL_TITLE, VALID_USER_ID);
    }
    
    // =================================================================
    // TESTES DE CRIAÇÃO
    // =================================================================

    @Test
    @DisplayName("Deve criar um Dashboard com sucesso quando o usuário for válido")
    void createDashboard_ShouldReturnDTO_WhenUserIsValid() throws UserNotFoundException {
        // ARRANGE
        DashboardTitleDTO dto = new DashboardTitleDTO(INITIAL_TITLE);
        
        // Configura mocks
        when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(validUser));
        when(dashboardRepository.save(any(Dashboard.class))).thenReturn(validDashboard);
        when(dashboardMapper.toResponseDTO(any(Dashboard.class))).thenReturn(validDashboardDTO);

        // ACT
        DashboardResponseDTO result = dashboardService.createDashboard(VALID_USER_ID, dto);

        // ASSERT
        assertNotNull(result);
        assertEquals(INITIAL_TITLE, result.title());
        verify(dashboardRepository, times(1)).save(any(Dashboard.class));
    }

    @Test
    @DisplayName("Deve lançar UserNotFoundException ao tentar criar com um ID de usuário inexistente")
    void createDashboard_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        // ARRANGE
        DashboardTitleDTO dto = new DashboardTitleDTO(INITIAL_TITLE);
        when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UserNotFoundException.class, () -> 
            dashboardService.createDashboard(VALID_USER_ID, dto)
        );
        verify(dashboardRepository, never()).save(any()); // Garante que não houve tentativa de salvar
    }
    
    // =================================================================
    // TESTES DE BUSCA
    // =================================================================

    @Test
    @DisplayName("Deve retornar o DTO quando o Dashboard for encontrado")
    void findDashboardById_ShouldReturnDTO_WhenFound() throws DashboardNotFoundException {
        // ARRANGE
        when(dashboardRepository.findById(VALID_DASHBOARD_ID)).thenReturn(Optional.of(validDashboard));
        when(dashboardMapper.toResponseDTO(any(Dashboard.class))).thenReturn(validDashboardDTO);

        // ACT
        DashboardResponseDTO result = dashboardService.findDashboardById(VALID_DASHBOARD_ID);

        // ASSERT
        assertNotNull(result);
        assertEquals(VALID_DASHBOARD_ID, result.id());
    }

    @Test
    @DisplayName("Deve lançar DashboardNotFoundException quando o Dashboard não for encontrado")
    void findDashboardById_ShouldThrowNotFound_WhenNotFound() {
        // ARRANGE
        when(dashboardRepository.findById(VALID_DASHBOARD_ID)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(DashboardNotFoundException.class, () -> 
            dashboardService.findDashboardById(VALID_DASHBOARD_ID)
        );
    }
    
    @Test
    @DisplayName("Deve retornar Set de DTOs quando o usuário tiver Dashboards")
    void findAllDashboardByUser_ShouldReturnSet_WhenDashboardsExist() throws UserNotFoundException {
        // ARRANGE
        Set<Dashboard> dashboardSet = Set.of(validDashboard);
        Set<DashboardResponseDTO> dtoList = Set.of(validDashboardDTO);
        
        when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(validUser));
        when(dashboardRepository.findByUser(validUser)).thenReturn(dashboardSet);
        when(dashboardMapper.toResponseAllDTO(dashboardSet)).thenReturn(dtoList);

        // ACT
        Set<DashboardResponseDTO> result = dashboardService.findAllDashboardByUser(VALID_USER_ID);

        // ASSERT
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Deve retornar Set vazio quando o usuário não tiver Dashboards")
    void findAllDashboardByUser_ShouldReturnEmptySet_WhenNoDashboardsExist() throws UserNotFoundException {
        // ARRANGE
        when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(validUser));
        when(dashboardRepository.findByUser(validUser)).thenReturn(Collections.emptySet());

        // ACT
        Set<DashboardResponseDTO> result = dashboardService.findAllDashboardByUser(VALID_USER_ID);

        // ASSERT
        assertTrue(result.isEmpty());
        verify(dashboardMapper, never()).toResponseAllDTO(any()); // Garante que o mapper não foi chamado com Set vazio
    }

    @Test
    @DisplayName("Deve lançar UserNotFoundException ao buscar Dashboards para usuário inexistente")
    void findAllDashboardByUser_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        // ARRANGE
        when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UserNotFoundException.class, () -> 
            dashboardService.findAllDashboardByUser(VALID_USER_ID)
        );
    }
    
    // =================================================================
    // TESTES DE EXCLUSAO
    // =================================================================
    
    @Test
    @DisplayName("Deve deletar o Dashboard com sucesso quando o usuário é o dono")
    void deleteDashboard_ShouldSucceed_WhenUserIsOwner() {
        // ARRANGE
        when(dashboardRepository.findById(VALID_DASHBOARD_ID)).thenReturn(Optional.of(validDashboard));

        // ACT
        assertDoesNotThrow(() -> 
            dashboardService.deleteDashboard(VALID_USER_ID, VALID_DASHBOARD_ID)
        );

        // ASSERT
        verify(dashboardRepository, times(1)).delete(validDashboard);
    }
    
    @Test
    @DisplayName("Deve lançar AccessDeniedException quando o usuário não é o dono")
    void deleteDashboard_ShouldThrowAccessDenied_WhenUserIsNotOwner() {
        // ARRANGE
        when(dashboardRepository.findById(VALID_DASHBOARD_ID)).thenReturn(Optional.of(validDashboard));

        // ACT & ASSERT
        assertThrows(AccessDeniedException.class, () -> 
            dashboardService.deleteDashboard(OTHER_USER_ID, VALID_DASHBOARD_ID) // Tentando deletar com OTHER_USER_ID
        );
        verify(dashboardRepository, never()).deleteById(any()); // Garante que a exclusão não ocorreu
    }

    @Test
    @DisplayName("Deve lançar DashboardNotFoundException ao tentar deletar Dashboard inexistente")
    void deleteDashboard_ShouldThrowNotFound_WhenDashboardDoesNotExist() {
        // ARRANGE
        when(dashboardRepository.findById(VALID_DASHBOARD_ID)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(DashboardNotFoundException.class, () -> 
            dashboardService.deleteDashboard(VALID_USER_ID, VALID_DASHBOARD_ID)
        );
        verify(dashboardRepository, never()).deleteById(any());
    }

    // =================================================================
    // TESTES DE EDICAO
    // =================================================================
    
    @Test
    @DisplayName("Deve editar o título com sucesso e retornar o DTO atualizado")
    void editTitle_ShouldUpdateAndReturnDTO_WhenUserIsOwner() throws AccessDeniedException, DashboardNotFoundException {
        // ARRANGE
        Dashboard updatedDashboard = new Dashboard(VALID_DASHBOARD_ID, NEW_TITLE, validUser);
        DashboardResponseDTO updatedDTO = new DashboardResponseDTO(VALID_DASHBOARD_ID, NEW_TITLE, VALID_USER_ID);

        when(dashboardRepository.findById(VALID_DASHBOARD_ID)).thenReturn(Optional.of(validDashboard));
        when(dashboardRepository.save(any(Dashboard.class))).thenReturn(updatedDashboard);
        when(dashboardMapper.toResponseDTO(any(Dashboard.class))).thenReturn(updatedDTO);

        // ACT
        DashboardResponseDTO result = dashboardService.editTitle(VALID_DASHBOARD_ID, VALID_USER_ID, NEW_TITLE);

        // ASSERT
        assertNotNull(result);
        assertEquals(NEW_TITLE, result.title());
        verify(dashboardRepository, times(1)).save(any(Dashboard.class));
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException ao tentar editar título sem ser o dono")
    void editTitle_ShouldThrowAccessDenied_WhenUserIsNotOwner() {
        // ARRANGE
        when(dashboardRepository.findById(VALID_DASHBOARD_ID)).thenReturn(Optional.of(validDashboard));

        // ACT & ASSERT
        assertThrows(AccessDeniedException.class, () -> 
            dashboardService.editTitle(VALID_DASHBOARD_ID, OTHER_USER_ID, NEW_TITLE)
        );
        verify(dashboardRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar DashboardNotFoundException ao tentar editar título inexistente")
    void editTitle_ShouldThrowNotFound_WhenDashboardDoesNotExist() {
        // ARRANGE
        when(dashboardRepository.findById(VALID_DASHBOARD_ID)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(DashboardNotFoundException.class, () -> 
            dashboardService.editTitle(VALID_DASHBOARD_ID, VALID_USER_ID, NEW_TITLE)
        );
        verify(dashboardRepository, never()).save(any());
    }
}
