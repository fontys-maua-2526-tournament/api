package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.Tournament;
import edu.fontysmaua.tournamentapi.domain.request.SaveTournamentRequest;
import edu.fontysmaua.tournamentapi.domain.request.AddTeamToTournament;
import edu.fontysmaua.tournamentapi.domain.request.RemoveTeamFromTournamentRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllTournamentsResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetTournamentByIdResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedTournamentResponse;
import edu.fontysmaua.tournamentapi.enums.Status;
import edu.fontysmaua.tournamentapi.exception.NameAlreadyExistsException;
import edu.fontysmaua.tournamentapi.mapper.TournamentMapper;
import edu.fontysmaua.tournamentapi.persistence.TournamentRepository;
import edu.fontysmaua.tournamentapi.persistence.TeamRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.TournamentEntity;
import edu.fontysmaua.tournamentapi.persistence.entity.TeamEntity;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentServiceImplTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private TournamentMapper tournamentMapper;

    @InjectMocks
    private TournamentServiceImpl tournamentService;

    @Mock
    private TeamRepository teamRepository;

    private TournamentEntity tournamentEntity;
    private TournamentEntity tournamentEntity2;
    private Tournament tournament;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private TeamEntity teamEntity;
    private TeamEntity teamEntity2;
    
    @BeforeEach
    void setUp() {
        startTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        endTime = LocalDateTime.of(2024, 1, 1, 18, 0);

        tournamentEntity = TournamentEntity.builder()
                .id(1L)
                .name("Spring Championship")
                .address("123 Main St")
                .startTime(startTime)
                .endTime(endTime)
                .status(Status.SCHEDULED)
                .teams(new ArrayList<>())
                .build();

        tournament = Tournament.builder()
                .id(1L)
                .name("Spring Championship")
                .address("123 Main St")
                .startTime(startTime)
                .endTime(endTime)
                .build();
        
        LocalDateTime futureStartTime = LocalDateTime.now().plusDays(1);
        tournamentEntity2 = TournamentEntity.builder()
                .id(2L)
                .name("Future Championship")
                .address("456 Future St")
                .startTime(futureStartTime)
                .endTime(futureStartTime.plusHours(8))
                .status(Status.SCHEDULED)
                .teams(new ArrayList<>())
                .build();

        teamEntity = TeamEntity.builder()
                .id(1L)
                .name("Team Alpha")
                .tournaments(new ArrayList<>())
                .build();

        teamEntity2 = TeamEntity.builder()
                .id(2L)
                .name("Team Beta")
                .tournaments(new ArrayList<>())
                .build();
    }

    // ==================== findAll() Tests ====================

    @Test
    void findAll_ShouldReturnAllTournaments_WhenTournamentsExist() {
        // Arrange
        TournamentEntity entity2 = TournamentEntity.builder()
                .id(2L)
                .name("Summer Championship")
                .address("456 Oak Ave")
                .startTime(startTime)
                .endTime(endTime)
                .build();

        List<TournamentEntity> entities = Arrays.asList(tournamentEntity, entity2);
        List<Tournament> tournaments = Arrays.asList(tournament,
                Tournament.builder()
                        .id(2L)
                        .name("Summer Championship")
                        .address("456 Oak Ave")
                        .startTime(startTime)
                        .endTime(endTime)
                        .build());

        when(tournamentRepository.findAll()).thenReturn(entities);
        when(tournamentMapper.entitiesToModels(entities)).thenReturn(tournaments);

        // Act
        GetAllTournamentsResponse response = tournamentService.findAll();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getTournaments().size());
        verify(tournamentRepository, times(1)).findAll();
        verify(tournamentMapper, times(1)).entitiesToModels(entities);
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoTournamentsExist() {
        // Arrange
        List<TournamentEntity> emptyList = new ArrayList<>();
        when(tournamentRepository.findAll()).thenReturn(emptyList);
        when(tournamentMapper.entitiesToModels(emptyList)).thenReturn(List.of());

        // Act
        GetAllTournamentsResponse response = tournamentService.findAll();

        // Assert
        assertNotNull(response);
        assertTrue(response.getTournaments().isEmpty());
        verify(tournamentRepository, times(1)).findAll();
    }

    // ==================== findById() Tests ====================

    @Test
    void findById_ShouldReturnTournament_WhenValidIdProvided() {
        // Arrange
        Long id = 1L;
        when(tournamentRepository.findById(id)).thenReturn(Optional.of(tournamentEntity));
        when(tournamentMapper.entityToModel(tournamentEntity)).thenReturn(tournament);

        // Act
        GetTournamentByIdResponse response = tournamentService.findById(id);

        // Assert
        assertNotNull(response);
        assertEquals(tournament, response.getTournament());
        verify(tournamentRepository, times(1)).findById(id);
        verify(tournamentMapper, times(1)).entityToModel(tournamentEntity);
    }

    @Test
    void findById_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tournamentService.findById(null)
        );

        assertEquals("ID cannot be null", exception.getMessage());
        verify(tournamentRepository, never()).findById(any());
    }

    @Test
    void findById_ShouldThrowIllegalArgumentException_WhenIdIsZero() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tournamentService.findById(0L)
        );

        assertEquals("ID must be greater than 0", exception.getMessage());
        verify(tournamentRepository, never()).findById(any());
    }

    @Test
    void findById_ShouldThrowIllegalArgumentException_WhenIdIsNegative() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tournamentService.findById(-1L)
        );

        assertEquals("ID must be greater than 0", exception.getMessage());
        verify(tournamentRepository, never()).findById(any());
    }

    @Test
    void findById_ShouldThrowEntityNotFoundException_WhenTournamentNotFound() {
        // Arrange
        Long id = 999L;
        when(tournamentRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> tournamentService.findById(id)
        );

        assertEquals("Tournament not found", exception.getMessage());
        verify(tournamentRepository, times(1)).findById(id);
        verify(tournamentMapper, never()).entityToModel(any());
    }

    // ==================== create() Tests ====================

    @Test
    void create_ShouldCreateTournamentWithScheduledStatus_WhenStartTimeIsInFuture() {
        // Arrange
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1);
        SaveTournamentRequest request = SaveTournamentRequest.builder()
                .name("New Tournament")
                .address("789 Pine Rd")
                .startTime(futureTime)
                .endTime(futureTime.plusHours(8))
                .build();

        TournamentEntity savedEntity = TournamentEntity.builder()
                .id(3L)
                .name("New Tournament")
                .address("789 Pine Rd")
                .startTime(futureTime)
                .endTime(futureTime.plusHours(8))
                .status(Status.SCHEDULED)
                .build();

        Tournament savedTournament = Tournament.builder()
                .id(3L)
                .name("New Tournament")
                .address("789 Pine Rd")
                .startTime(futureTime)
                .endTime(futureTime.plusHours(8))
                .build();

        when(tournamentRepository.existsByName("New Tournament")).thenReturn(false);
        when(tournamentRepository.save(any(TournamentEntity.class))).thenReturn(savedEntity);
        when(tournamentMapper.entityToModel(savedEntity)).thenReturn(savedTournament);

        // Act
        SavedTournamentResponse response = tournamentService.create(request);

        // Assert
        assertNotNull(response);
        assertEquals(savedTournament, response.getTournament());

        ArgumentCaptor<TournamentEntity> entityCaptor = ArgumentCaptor.forClass(TournamentEntity.class);
        verify(tournamentRepository, times(1)).save(entityCaptor.capture());
        assertEquals(Status.SCHEDULED, entityCaptor.getValue().getStatus());

        verify(tournamentRepository, times(1)).existsByName("New Tournament");
        verify(tournamentMapper, times(1)).entityToModel(savedEntity);
    }

    @Test
    void create_ShouldCreateTournamentWithCompletedStatus_WhenStartTimeIsInPast() {
        // Arrange
        LocalDateTime pastTime = LocalDateTime.now().minusDays(1);
        SaveTournamentRequest request = SaveTournamentRequest.builder()
                .name("Past Tournament")
                .address("789 Pine Rd")
                .startTime(pastTime)
                .endTime(pastTime.plusHours(8))
                .build();

        TournamentEntity savedEntity = TournamentEntity.builder()
                .id(3L)
                .name("Past Tournament")
                .address("789 Pine Rd")
                .startTime(pastTime)
                .endTime(pastTime.plusHours(8))
                .status(Status.COMPLETED)
                .build();

        Tournament savedTournament = Tournament.builder()
                .id(3L)
                .name("Past Tournament")
                .address("789 Pine Rd")
                .startTime(pastTime)
                .endTime(pastTime.plusHours(8))
                .build();

        when(tournamentRepository.existsByName("Past Tournament")).thenReturn(false);
        when(tournamentRepository.save(any(TournamentEntity.class))).thenReturn(savedEntity);
        when(tournamentMapper.entityToModel(savedEntity)).thenReturn(savedTournament);

        // Act
        SavedTournamentResponse response = tournamentService.create(request);

        // Assert
        assertNotNull(response);
        assertEquals(savedTournament, response.getTournament());

        ArgumentCaptor<TournamentEntity> entityCaptor = ArgumentCaptor.forClass(TournamentEntity.class);
        verify(tournamentRepository, times(1)).save(entityCaptor.capture());
        assertEquals(Status.COMPLETED, entityCaptor.getValue().getStatus());
    }

    @Test
    void create_ShouldCreateTournamentWithNullStatus_WhenStartTimeIsNull() {
        // Arrange
        SaveTournamentRequest request = SaveTournamentRequest.builder()
                .name("No Time Tournament")
                .address("789 Pine Rd")
                .startTime(null)
                .endTime(endTime)
                .build();

        TournamentEntity savedEntity = TournamentEntity.builder()
                .id(3L)
                .name("No Time Tournament")
                .address("789 Pine Rd")
                .startTime(null)
                .endTime(endTime)
                .status(null)
                .build();

        Tournament savedTournament = Tournament.builder()
                .id(3L)
                .name("No Time Tournament")
                .address("789 Pine Rd")
                .startTime(null)
                .endTime(endTime)
                .build();

        when(tournamentRepository.existsByName("No Time Tournament")).thenReturn(false);
        when(tournamentRepository.save(any(TournamentEntity.class))).thenReturn(savedEntity);
        when(tournamentMapper.entityToModel(savedEntity)).thenReturn(savedTournament);

        // Act
        SavedTournamentResponse response = tournamentService.create(request);

        // Assert
        assertNotNull(response);
        assertEquals(savedTournament, response.getTournament());

        ArgumentCaptor<TournamentEntity> entityCaptor = ArgumentCaptor.forClass(TournamentEntity.class);
        verify(tournamentRepository, times(1)).save(entityCaptor.capture());
        assertNull(entityCaptor.getValue().getStatus());
    }

    @Test
    void create_ShouldThrowNameAlreadyExistsException_WhenNameAlreadyExists() {
        // Arrange
        SaveTournamentRequest request = SaveTournamentRequest.builder()
                .name("Existing Tournament")
                .address("789 Pine Rd")
                .startTime(startTime)
                .endTime(endTime)
                .build();

        when(tournamentRepository.existsByName("Existing Tournament")).thenReturn(true);

        // Act & Assert
        assertThrows(NameAlreadyExistsException.class, () -> tournamentService.create(request));

        verify(tournamentRepository, times(1)).existsByName("Existing Tournament");
        verify(tournamentRepository, never()).save(any());
        verify(tournamentMapper, never()).entityToModel(any());
    }

    // ==================== update() Tests ====================

    @Test
    void update_ShouldUpdateTournament_WhenValidRequestProvided() {
        // Arrange
        SaveTournamentRequest updateRequest = SaveTournamentRequest.builder()
                .id(1L)
                .name("Updated Tournament")
                .address("Updated Address")
                .startTime(startTime)
                .endTime(endTime)
                .build();

        TournamentEntity updatedEntity = TournamentEntity.builder()
                .id(1L)
                .name("Updated Tournament")
                .address("Updated Address")
                .startTime(startTime)
                .endTime(endTime)
                .build();

        Tournament updatedTournament = Tournament.builder()
                .id(1L)
                .name("Updated Tournament")
                .address("Updated Address")
                .startTime(startTime)
                .endTime(endTime)
                .build();

        when(tournamentRepository.existsById(1L)).thenReturn(true);
        when(tournamentRepository.save(any(TournamentEntity.class))).thenReturn(updatedEntity);
        when(tournamentMapper.entityToModel(updatedEntity)).thenReturn(updatedTournament);

        // Act
        SavedTournamentResponse response = tournamentService.update(updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(updatedTournament, response.getTournament());
        verify(tournamentRepository, times(1)).existsById(1L);
        verify(tournamentRepository, times(1)).save(any(TournamentEntity.class));
        verify(tournamentMapper, times(1)).entityToModel(updatedEntity);
    }

    @Test
    void update_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
        // Arrange
        SaveTournamentRequest request = SaveTournamentRequest.builder()
                .id(null)
                .name("Updated Tournament")
                .address("Updated Address")
                .startTime(startTime)
                .endTime(endTime)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tournamentService.update(request)
        );

        assertEquals("Tournament ID cannot be null or zero", exception.getMessage());
        verify(tournamentRepository, never()).existsById(any());
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void update_ShouldThrowEntityNotFoundException_WhenTournamentDoesNotExist() {
        // Arrange
        SaveTournamentRequest request = SaveTournamentRequest.builder()
                .id(999L)
                .name("Updated Tournament")
                .address("Updated Address")
                .startTime(startTime)
                .endTime(endTime)
                .build();

        when(tournamentRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> tournamentService.update(request)
        );

        assertEquals("Tournament doesn't exist in the database", exception.getMessage());
        verify(tournamentRepository, times(1)).existsById(999L);
        verify(tournamentRepository, never()).save(any());
    }

    // ==================== cancel() Tests ====================

    @Test
    void cancel_ShouldReturnTournamentId_WhenTournamentExists() {
        // Arrange
        Long tournamentId = 1L;
        TournamentEntity tournament = TournamentEntity.builder()
                .id(tournamentId)
                .name("Spring Championship")
                .address("123 Main St")
                .startTime(startTime)
                .endTime(endTime)
                .status(Status.SCHEDULED)
                .build();

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(TournamentEntity.class))).thenReturn(tournament);

        // Act
        Long result = tournamentService.cancel(tournamentId);

        // Assert
        assertEquals(tournamentId, result);
        assertEquals(Status.CANCELLED, tournament.getStatus());
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(tournamentRepository, times(1)).save(tournament);
    }

    @Test
    void cancel_ShouldThrowEntityNotFoundException_WhenTournamentDoesNotExist() {
        // Arrange
        Long tournamentId = 999L;
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> tournamentService.cancel(tournamentId)
        );

        assertEquals("Tournament not found", exception.getMessage());
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(tournamentRepository, never()).save(any(TournamentEntity.class));
    }

    @Test
    void cancel_ShouldChangeStatusToCancelled_WhenTournamentHasDifferentStatus() {
        // Arrange
        Long tournamentId = 2L;
        TournamentEntity tournament = TournamentEntity.builder()
                .id(tournamentId)
                .name("Summer Championship")
                .address("456 Oak Ave")
                .startTime(startTime)
                .endTime(endTime)
                .status(Status.COMPLETED)
                .build();

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(TournamentEntity.class))).thenReturn(tournament);

        // Act
        Long result = tournamentService.cancel(tournamentId);

        // Assert
        assertEquals(tournamentId, result);
        assertEquals(Status.CANCELLED, tournament.getStatus());
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(tournamentRepository, times(1)).save(tournament);
    }

    @Test
    void cancel_ShouldSaveWithCancelledStatus_WhenAlreadyCancelled() {
        // Arrange
        Long tournamentId = 3L;
        TournamentEntity tournament = TournamentEntity.builder()
                .id(tournamentId)
                .name("Winter Championship")
                .address("789 Pine Rd")
                .startTime(startTime)
                .endTime(endTime)
                .status(Status.CANCELLED)
                .build();

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(TournamentEntity.class))).thenReturn(tournament);

        // Act
        Long result = tournamentService.cancel(tournamentId);

        // Assert
        assertEquals(tournamentId, result);
        assertEquals(Status.CANCELLED, tournament.getStatus());
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(tournamentRepository, times(1)).save(tournament);
    }

    // ==================== addTeam() Tests ====================

    @Test
    void addTeam_ShouldAddTeamSuccessfully_WhenValidRequest() {
        // Arrange
        AddTeamToTournament request = new AddTeamToTournament(1L, 1L);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(teamEntity));
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournamentEntity2));
        when(tournamentRepository.save(any(TournamentEntity.class))).thenReturn(tournamentEntity2);
        when(teamRepository.save(any(TeamEntity.class))).thenReturn(teamEntity);

        // Act
        Boolean result = tournamentService.addTeam(request);

        // Assert
        assertTrue(result);
        assertTrue(tournamentEntity2.getTeams().contains(teamEntity));
        assertTrue(teamEntity.getTournaments().contains(tournamentEntity2));

        verify(teamRepository, times(1)).findById(1L);
        verify(tournamentRepository, times(1)).findById(1L);
        verify(tournamentRepository, times(1)).save(tournamentEntity2);
        verify(teamRepository, times(1)).save(teamEntity);
    }

    @Test
    void addTeam_ShouldThrowIllegalArgumentException_WhenTeamNotFound() {
        // Arrange
        AddTeamToTournament request = new AddTeamToTournament(999L, 1L);

        when(teamRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tournamentService.addTeam(request)
        );

        assertEquals("Team not found with ID: 999", exception.getMessage());
        verify(teamRepository, times(1)).findById(999L);
        verify(tournamentRepository, never()).findById(any());
    }

    @Test
    void addTeam_ShouldThrowIllegalArgumentException_WhenTournamentNotFound() {
        // Arrange
        AddTeamToTournament request = new AddTeamToTournament(1L, 999L);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(teamEntity));
        when(tournamentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tournamentService.addTeam(request)
        );

        assertEquals("Tournament not found with ID: 999", exception.getMessage());
        verify(teamRepository, times(1)).findById(1L);
        verify(tournamentRepository, times(1)).findById(999L);
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void addTeam_ShouldThrowIllegalStateException_WhenTournamentAlreadyStarted() {
        // Arrange
        AddTeamToTournament request = new AddTeamToTournament(1L, 1L);
        
        TournamentEntity tournamentComDataPassada = TournamentEntity.builder()
                .id(1L)
                .name("Torneio Passado")
                .startTime(LocalDateTime.now().minusHours(1))
                .teams(new ArrayList<>())
                .build();
        
        when(teamRepository.findById(1L)).thenReturn(Optional.of(teamEntity));
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournamentComDataPassada));

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> tournamentService.addTeam(request)
        );

        assertEquals("Cannot register team after tournament has started", exception.getMessage());
        verify(teamRepository, times(1)).findById(1L);
        verify(tournamentRepository, times(1)).findById(1L);
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void addTeam_ShouldThrowIllegalStateException_WhenTeamAlreadyRegistered() {
        // Arrange
        AddTeamToTournament request = new AddTeamToTournament(1L, 1L);

        tournamentEntity2.getTeams().add(teamEntity);
        teamEntity.getTournaments().add(tournamentEntity2);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(teamEntity));
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournamentEntity2));

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> tournamentService.addTeam(request)
        );

        assertTrue(exception.getMessage().contains("is already registered in tournament"));
        verify(teamRepository, times(1)).findById(1L);
        verify(tournamentRepository, times(1)).findById(1L);
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void addTeam_ShouldInitializeListsWhenNull() {
        // Arrange
        AddTeamToTournament request = new AddTeamToTournament(1L, 1L);

        tournamentEntity2.setTeams(null);
        teamEntity.setTournaments(null);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(teamEntity));
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournamentEntity2));
        when(tournamentRepository.save(any(TournamentEntity.class))).thenReturn(tournamentEntity2);
        when(teamRepository.save(any(TeamEntity.class))).thenReturn(teamEntity);

        // Act
        Boolean result = tournamentService.addTeam(request);

        // Assert
        assertTrue(result);
        assertNotNull(tournamentEntity2.getTeams());
        assertNotNull(teamEntity.getTournaments());
        assertEquals(1, tournamentEntity2.getTeams().size());
        assertEquals(1, teamEntity.getTournaments().size());
    }

    // ==================== removeTeam() Tests ====================

    @Test
    void removeTeam_ShouldRemoveTeamSuccessfully_WhenValidRequest() {
        // Arrange
        RemoveTeamFromTournamentRequest request = new RemoveTeamFromTournamentRequest(1L, 2L);

        tournamentEntity2.getTeams().add(teamEntity);
        teamEntity.getTournaments().add(tournamentEntity2);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(teamEntity));
        when(tournamentRepository.findById(2L)).thenReturn(Optional.of(tournamentEntity2));
        when(tournamentRepository.save(any(TournamentEntity.class))).thenReturn(tournamentEntity2);
        when(teamRepository.save(any(TeamEntity.class))).thenReturn(teamEntity);

        // Act
        Boolean result = tournamentService.removeTeam(request);

        // Assert
        assertTrue(result);
        assertFalse(tournamentEntity2.getTeams().contains(teamEntity));
        assertFalse(teamEntity.getTournaments().contains(tournamentEntity2));

        verify(teamRepository, times(1)).findById(1L);
        verify(tournamentRepository, times(1)).findById(2L);
        verify(tournamentRepository, times(1)).save(tournamentEntity2);
        verify(teamRepository, times(1)).save(teamEntity);
    }

    @Test
    void removeTeam_ShouldThrowIllegalArgumentException_WhenTeamNotFound() {
        // Arrange
        RemoveTeamFromTournamentRequest request = new RemoveTeamFromTournamentRequest(999L, 1L);

        when(teamRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tournamentService.removeTeam(request)
        );

        assertEquals("Team not found with ID: 999", exception.getMessage());
        verify(teamRepository, times(1)).findById(999L);
        verify(tournamentRepository, never()).findById(any());
    }

    @Test
    void removeTeam_ShouldThrowIllegalArgumentException_WhenTournamentNotFound() {
        // Arrange
        RemoveTeamFromTournamentRequest request = new RemoveTeamFromTournamentRequest(1L, 999L);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(teamEntity));
        when(tournamentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tournamentService.removeTeam(request)
        );

        assertEquals("Tournament not found with ID: 999", exception.getMessage());
        verify(teamRepository, times(1)).findById(1L);
        verify(tournamentRepository, times(1)).findById(999L);
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void removeTeam_ShouldThrowIllegalStateException_WhenTournamentAlreadyStarted() {
        // Arrange
        RemoveTeamFromTournamentRequest request = new RemoveTeamFromTournamentRequest(1L, 1L);

        tournamentEntity2.setStartTime(LocalDateTime.now().minusHours(1));
        tournamentEntity2.getTeams().add(teamEntity);
        teamEntity.getTournaments().add(tournamentEntity2);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(teamEntity));
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournamentEntity2));

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> tournamentService.removeTeam(request)
        );

        assertEquals("Cannot remove team after tournament has started", exception.getMessage());
        verify(teamRepository, times(1)).findById(1L);
        verify(tournamentRepository, times(1)).findById(1L);
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void removeTeam_ShouldThrowIllegalStateException_WhenTeamNotRegistered() {
        // Arrange
        RemoveTeamFromTournamentRequest request = new RemoveTeamFromTournamentRequest(1L, 1L);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(teamEntity));
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournamentEntity2));

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> tournamentService.removeTeam(request)
        );

        assertTrue(exception.getMessage().contains("is not registered in tournament"));
        verify(teamRepository, times(1)).findById(1L);
        verify(tournamentRepository, times(1)).findById(1L);
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void removeTeam_ShouldHandleNullListsGracefully() {
        // Arrange
        RemoveTeamFromTournamentRequest request = new RemoveTeamFromTournamentRequest(1L, 1L);

        tournamentEntity2.setTeams(null);
        teamEntity.setTournaments(null);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(teamEntity));
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournamentEntity2));

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> tournamentService.removeTeam(request)
        );

        assertTrue(exception.getMessage().contains("is not registered in tournament"));
    }

    @Test
    void removeTeam_ShouldRemoveFromMultipleTeams() {
        // Arrange
        RemoveTeamFromTournamentRequest request = new RemoveTeamFromTournamentRequest(1L, 1L);

        tournamentEntity2.getTeams().add(teamEntity);
        tournamentEntity2.getTeams().add(teamEntity2);
        teamEntity.getTournaments().add(tournamentEntity2);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(teamEntity));
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournamentEntity2));
        when(tournamentRepository.save(any(TournamentEntity.class))).thenReturn(tournamentEntity2);
        when(teamRepository.save(any(TeamEntity.class))).thenReturn(teamEntity);

        // Act
        Boolean result = tournamentService.removeTeam(request);

        // Assert
        assertTrue(result);
        assertFalse(tournamentEntity2.getTeams().contains(teamEntity));
        assertTrue(tournamentEntity2.getTeams().contains(teamEntity2)); // O outro time permanece
        assertEquals(1, tournamentEntity2.getTeams().size());
    }
}