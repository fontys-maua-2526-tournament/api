package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.Tournament;
import edu.fontysmaua.tournamentapi.domain.Match;
import edu.fontysmaua.tournamentapi.domain.request.SaveTournamentRequest;
import edu.fontysmaua.tournamentapi.domain.request.AddTeamToTournament;
import edu.fontysmaua.tournamentapi.domain.request.RemoveTeamFromTournamentRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllTournamentsResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetMatchesByTournamentRoundResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetTournamentByIdResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedTournamentResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetMatchesByTournamentRoundResponse;
import edu.fontysmaua.tournamentapi.enums.Status;
import edu.fontysmaua.tournamentapi.mapper.TournamentMapper;
import edu.fontysmaua.tournamentapi.persistence.MatchRepository;
import edu.fontysmaua.tournamentapi.persistence.TournamentRepository;
import edu.fontysmaua.tournamentapi.persistence.TeamRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.TournamentEntity;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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

    @Mock
    private MatchRepository matchRepository;
    @Mock
    private MatchMapper matchMapper;

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

        SaveTournamentRequest.builder()
                .id(1L)
                .name("Spring Championship")
                .address("123 Main St")
                .startTime(startTime)
                .endTime(endTime)
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

        when(tournamentRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))).thenReturn(entities);
        when(tournamentMapper.entitiesToModels(entities)).thenReturn(tournaments);

        // Act
        GetAllTournamentsResponse response = tournamentService.findAll();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getTournaments().size());
        verify(tournamentRepository, times(1)).findAll(Sort.by(Sort.Direction.ASC, "id"));
        verify(tournamentMapper, times(1)).entitiesToModels(entities);
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoTournamentsExist() {
        // Arrange
        List<TournamentEntity> emptyList = new ArrayList<>();
        when(tournamentRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))).thenReturn(emptyList);
        when(tournamentMapper.entitiesToModels(emptyList)).thenReturn(List.of());

        // Act
        GetAllTournamentsResponse response = tournamentService.findAll();

        // Assert
        assertNotNull(response);
        assertTrue(response.getTournaments().isEmpty());
        verify(tournamentRepository, times(1)).findAll(Sort.by(Sort.Direction.ASC, "id"));
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
        assertThrows(EntityExistsException.class, () -> tournamentService.create(request));

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

    // ==================== getTournamentMatchesByRound() Tests ====================

    @Test
    void getTournamentMatchesByRound_ShouldReturnMatches_WhenTournamentExistsAndHasMatchesInRound() {
        // Arrange
        Long tournamentId = 1L;
        Integer round = 1;
        
        MatchEntity match1 = MatchEntity.builder()
                .id(1L)
                .round(1)
                .team1Score(2)
                .team2Score(1)
                .status(Status.SCHEDULED)
                .tournament(tournamentEntity)
                .build();
        
        MatchEntity match2 = MatchEntity.builder()
                .id(2L)
                .round(1)
                .team1Score(0)
                .team2Score(0)
                .status(Status.PENDING)
                .tournament(tournamentEntity)
                .build();
        
        List<MatchEntity> matchEntities = List.of(match1, match2);
        
        Match matchModel1 = Match.builder()
                .id(1L)
                .round(1)
                .team1Score(2)
                .team2Score(1)
                .status(Status.SCHEDULED)
                .build();
        
        Match matchModel2 = Match.builder()
                .id(2L)
                .round(1)
                .team1Score(0)
                .team2Score(0)
                .status(Status.PENDING)
                .build();
        
        List<Match> matches = List.of(matchModel1, matchModel2);
        
        when(tournamentRepository.existsById(tournamentId)).thenReturn(true);
        when(matchRepository.findByTournamentIdAndRound(tournamentId, round)).thenReturn(matchEntities);
        when(matchMapper.entitiesToModels(matchEntities)).thenReturn(matches);
        
        // Act
        GetMatchesByTournamentRoundResponse response = 
                tournamentService.getTournamentMatchesByRound(tournamentId, round);
        
        // Assert
        assertNotNull(response);
        assertEquals(tournamentId, response.getTournamentId());
        assertEquals(round, response.getRound());
        assertEquals(2, response.getMatches().size());
        assertEquals(matchModel1, response.getMatches().get(0));
        assertEquals(matchModel2, response.getMatches().get(1));
        
        verify(tournamentRepository, times(1)).existsById(tournamentId);
        verify(matchRepository, times(1)).findByTournamentIdAndRound(tournamentId, round);
        verify(matchMapper, times(1)).entitiesToModels(matchEntities);
    }

    @Test
    void getTournamentMatchesByRound_ShouldReturnEmptyList_WhenNoMatchesInRound() {
        // Arrange
        Long tournamentId = 1L;
        Integer round = 5; // Final, sem partidas ainda
        
        when(tournamentRepository.existsById(tournamentId)).thenReturn(true);
        when(matchRepository.findByTournamentIdAndRound(tournamentId, round)).thenReturn(List.of());
        when(matchMapper.entitiesToModels(anyList())).thenReturn(List.of());
        
        // Act
        GetMatchesByTournamentRoundResponse response = 
                tournamentService.getTournamentMatchesByRound(tournamentId, round);
        
        // Assert
        assertNotNull(response);
        assertEquals(tournamentId, response.getTournamentId());
        assertEquals(round, response.getRound());
        assertTrue(response.getMatches().isEmpty());
        
        verify(tournamentRepository, times(1)).existsById(tournamentId);
        verify(matchRepository, times(1)).findByTournamentIdAndRound(tournamentId, round);
        verify(matchMapper, times(1)).entitiesToModels(anyList());
    }

    @Test
    void getTournamentMatchesByRound_ShouldReturnAllMatches_WhenRoundIsNull() {
        // Arrange
        Long tournamentId = 1L;
        
        MatchEntity match1 = MatchEntity.builder()
                .id(1L).round(1).tournament(tournamentEntity).build();
        MatchEntity match2 = MatchEntity.builder()
                .id(2L).round(2).tournament(tournamentEntity).build();
        MatchEntity match3 = MatchEntity.builder()
                .id(3L).round(3).tournament(tournamentEntity).build();
        
        List<MatchEntity> allMatches = List.of(match1, match2, match3);
        
        List<Match> allMatchModels = List.of(
                Match.builder().id(1L).round(1).build(),
                Match.builder().id(2L).round(2).build(),
                Match.builder().id(3L).round(3).build()
        );
        
        when(tournamentRepository.existsById(tournamentId)).thenReturn(true);
        when(matchRepository.findByTournamentId(tournamentId)).thenReturn(allMatches);
        when(matchMapper.entitiesToModels(allMatches)).thenReturn(allMatchModels);
        
        // Act
        GetMatchesByTournamentRoundResponse response = 
                tournamentService.getTournamentMatchesByRound(tournamentId, null);
        
        // Assert
        assertNotNull(response);
        assertEquals(tournamentId, response.getTournamentId());
        assertNull(response.getRound()); // round é null quando busca todas
        assertEquals(3, response.getMatches().size());
        
        verify(tournamentRepository, times(1)).existsById(tournamentId);
        verify(matchRepository, times(1)).findByTournamentId(tournamentId);
        verify(matchRepository, never()).findByTournamentIdAndRound(any(), any());
        verify(matchMapper, times(1)).entitiesToModels(allMatches);
    }

    @Test
    void getTournamentMatchesByRound_ShouldThrowException_WhenTournamentNotFound() {
        // Arrange
        Long tournamentId = 999L;
        Integer round = 1;
        
        when(tournamentRepository.existsById(tournamentId)).thenReturn(false);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tournamentService.getTournamentMatchesByRound(tournamentId, round)
        );
        
        assertEquals("Tournament not found with ID: 999", exception.getMessage());
        
        verify(tournamentRepository, times(1)).existsById(tournamentId);
        verify(matchRepository, never()).findByTournamentIdAndRound(any(), any());
        verify(matchMapper, never()).entitiesToModels(anyList());
    }

    @Test
    void getTournamentMatchesByRound_ShouldThrowException_WhenTournamentIdIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tournamentService.getTournamentMatchesByRound(null, 1)
        );
        
        assertEquals("Tournament ID must be greater than 0", exception.getMessage());
        
        verify(tournamentRepository, never()).existsById(any());
        verify(matchRepository, never()).findByTournamentIdAndRound(any(), any());
    }

    @Test
    void getTournamentMatchesByRound_ShouldThrowException_WhenTournamentIdIsZero() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tournamentService.getTournamentMatchesByRound(0L, 1)
        );
        
        assertEquals("Tournament ID must be greater than 0", exception.getMessage());
        
        verify(tournamentRepository, never()).existsById(any());
        verify(matchRepository, never()).findByTournamentIdAndRound(any(), any());
    }

    @Test
    void getTournamentMatchesByRound_ShouldThrowException_WhenTournamentIdIsNegative() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tournamentService.getTournamentMatchesByRound(-1L, 1)
        );
        
        assertEquals("Tournament ID must be greater than 0", exception.getMessage());
        
        verify(tournamentRepository, never()).existsById(any());
        verify(matchRepository, never()).findByTournamentIdAndRound(any(), any());
    }

    @Test
    void getTournamentMatchesByRound_ShouldThrowException_WhenRoundIsNegative() {
        // Arrange
        Long tournamentId = 1L;
        Integer round = -1;
        
        when(tournamentRepository.existsById(tournamentId)).thenReturn(true);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tournamentService.getTournamentMatchesByRound(tournamentId, round)
        );
        
        assertEquals("Round must be a non-negative integer", exception.getMessage());
        
        verify(tournamentRepository, times(1)).existsById(tournamentId);
        verify(matchRepository, never()).findByTournamentIdAndRound(any(), any());
    }

    @Test
    void getTournamentMatchesByRound_ShouldReturnMatchesFromSpecificRoundOnly() {
        // Arrange
        Long tournamentId = 1L;
        Integer round = 2;
        
        MatchEntity matchRound1 = MatchEntity.builder()
                .id(1L).round(1).tournament(tournamentEntity).build();
        MatchEntity matchRound2a = MatchEntity.builder()
                .id(2L).round(2).tournament(tournamentEntity).build();
        MatchEntity matchRound2b = MatchEntity.builder()
                .id(3L).round(2).tournament(tournamentEntity).build();
        MatchEntity matchRound3 = MatchEntity.builder()
                .id(4L).round(3).tournament(tournamentEntity).build();
        
        List<MatchEntity> round2Matches = List.of(matchRound2a, matchRound2b);
        
        List<Match> round2MatchModels = List.of(
                Match.builder().id(2L).round(2).build(),
                Match.builder().id(3L).round(2).build()
        );
        
        when(tournamentRepository.existsById(tournamentId)).thenReturn(true);
        when(matchRepository.findByTournamentIdAndRound(tournamentId, round)).thenReturn(round2Matches);
        when(matchMapper.entitiesToModels(round2Matches)).thenReturn(round2MatchModels);
        
        // Act
        GetMatchesByTournamentRoundResponse response = 
                tournamentService.getTournamentMatchesByRound(tournamentId, round);
        
        // Assert
        assertNotNull(response);
        assertEquals(tournamentId, response.getTournamentId());
        assertEquals(round, response.getRound());
        assertEquals(2, response.getMatches().size());
        
        assertTrue(response.getMatches().stream().allMatch(m -> m.getRound() == 2));
        
        verify(matchRepository, times(1)).findByTournamentIdAndRound(tournamentId, round);
    }

    @Test
    void getTournamentMatchesByRound_ShouldHandleMultipleTournamentsCorrectly() {
        // Arrange
        Long tournamentId1 = 1L;
        Long tournamentId2 = 2L;
        Integer round = 1;
        
        TournamentEntity tournament2 = TournamentEntity.builder()
                .id(2L)
                .name("Another Tournament")
                .build();
        
        // Partidas do torneio 1
        MatchEntity match1 = MatchEntity.builder()
                .id(1L).round(1).tournament(tournamentEntity).build();
        
        // Partidas do torneio 2 (não devem ser retornadas)
        MatchEntity match2 = MatchEntity.builder()
                .id(2L).round(1).tournament(tournament2).build();
        
        List<MatchEntity> tournament1Matches = List.of(match1);
        List<Match> tournament1MatchModels = List.of(
                Match.builder().id(1L).round(1).build()
        );
        
        when(tournamentRepository.existsById(tournamentId1)).thenReturn(true);
        when(matchRepository.findByTournamentIdAndRound(tournamentId1, round)).thenReturn(tournament1Matches);
        when(matchMapper.entitiesToModels(tournament1Matches)).thenReturn(tournament1MatchModels);
        
        // Act
        GetMatchesByTournamentRoundResponse response = 
                tournamentService.getTournamentMatchesByRound(tournamentId1, round);
        
        // Assert
        assertNotNull(response);
        assertEquals(tournamentId1, response.getTournamentId());
        assertEquals(1, response.getMatches().size());
        assertEquals(1L, response.getMatches().get(0).getId());
    }
}