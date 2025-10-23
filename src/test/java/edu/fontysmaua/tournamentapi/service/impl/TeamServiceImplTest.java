package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.Team.Team;
import edu.fontysmaua.tournamentapi.domain.Team.request.SaveTeamRequest;
import edu.fontysmaua.tournamentapi.domain.Team.response.GetAllTeamsResponse;
import edu.fontysmaua.tournamentapi.mapper.TeamMapper;
import edu.fontysmaua.tournamentapi.persistence.TeamRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.TeamEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceImplTest {

    @Mock
    private TeamRepository teamRepository;
    @Mock
    private TeamMapper teamMapper;
    @InjectMocks
    private TeamServiceImpl teamService;

    private TeamEntity teamEntity;
    private Team team;

    @BeforeEach
    void setUp() {
        teamEntity = new TeamEntity();
        teamEntity.setId(1L);
        teamEntity.setName("Team 1");

        team = new Team();
        team.setId(teamEntity.getId());
        team.setName(teamEntity.getName());
    }

    // --- getAll() tests ---

    @Test
    void getAll_ShouldReturnMappedTeams_WhenRepositoryReturnsEntities() {
        // Arrange
        TeamEntity entity = new TeamEntity();
        Team model = new Team();

        when(teamRepository.findAll()).thenReturn(List.of(entity));
        when(teamMapper.entitiesToModels(anyList())).thenReturn(List.of(model));

        // Act
        GetAllTeamsResponse response = teamService.findAll();

        // Assert
        assertNotNull(response);
        assertNotNull(response.getTeams());
        assertEquals(1, response.getTeams().size());
        assertEquals(model, response.getTeams().getFirst());

        verify(teamRepository, times(1)).findAll();
        verify(teamMapper, times(1)).entitiesToModels(anyList());
    }

    @Test
    void getAll_ShouldReturnEmptyList_WhenRepositoryReturnsEmpty() {
        when(teamRepository.findAll()).thenReturn(Collections.emptyList());
        when(teamMapper.entitiesToModels(anyList())).thenReturn(Collections.emptyList());

        GetAllTeamsResponse response = teamService.findAll();

        assertNotNull(response);
        assertTrue(response.getTeams().isEmpty());

        verify(teamRepository, times(1)).findAll();
        verify(teamMapper, times(1)).entitiesToModels(anyList());
    }

    // --- delete() tests ---

    @Test
    void delete_ShouldThrowException_WhenIdIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> teamService.delete(null));

        assertEquals("ID cannot be null.", exception.getMessage());
        verify(teamRepository, never()).deleteById(any());
    }

    @Test
    void delete_ShouldThrowException_WhenIdIsZeroOrNegative() {
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class,
                () -> teamService.delete(0L));
        assertEquals("ID must be greater than 0.", exception1.getMessage());

        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class,
                () -> teamService.delete(-5L));
        assertEquals("ID must be greater than 0.", exception2.getMessage());

        verify(teamRepository, never()).deleteById(any());
    }

    @Test
    void delete_ShouldCallRepositoryAndReturnId_WhenIdIsValid() {
        Long id = 10L;

        Long result = teamService.delete(id);

        assertEquals(id, result);

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(teamRepository, times(1)).deleteById(idCaptor.capture());
        assertEquals(id, idCaptor.getValue());
    }


    @Test
    void updateTeam_fail() {
        assertThrows(IllegalArgumentException.class, () -> teamService.update(new SaveTeamRequest(team.getId(), team.getName())));
        verify(teamRepository, times(1)).existsById(any(Long.class));
        verify(teamRepository, times(0)).save(any(TeamEntity.class));
    }

    @Test
    void updateTeam_success() {
        when(teamRepository.existsById(any(Long.class))).thenReturn(true);
        when(teamRepository.save(any(TeamEntity.class))).thenReturn(teamEntity);

        assertDoesNotThrow(() -> teamService.update(new SaveTeamRequest(teamEntity.getId(), teamEntity.getName())));
        verify(teamRepository, times(1)).existsById(any(Long.class));
        verify(teamRepository, times(1)).save(any(TeamEntity.class));
    }

    @Test
    void createTeam_success() {
        when(teamRepository.save(any(TeamEntity.class))).thenReturn(teamEntity);

        teamService.create(new SaveTeamRequest(teamEntity.getId(), teamEntity.getName()));

        verify(teamRepository, times(1)).save(any(TeamEntity.class));
    }
}
