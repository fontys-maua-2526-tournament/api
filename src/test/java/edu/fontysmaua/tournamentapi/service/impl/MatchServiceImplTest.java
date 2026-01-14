package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.Match;
import edu.fontysmaua.tournamentapi.domain.request.SaveMatchRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllMatchesResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetAllUpcomingMatchesResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetMatchByIdResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedMatchResponse;
import edu.fontysmaua.tournamentapi.enums.Status;
import edu.fontysmaua.tournamentapi.mapper.MatchMapper;
import edu.fontysmaua.tournamentapi.persistence.MatchRepository;
import edu.fontysmaua.tournamentapi.persistence.TeamRepository;
import edu.fontysmaua.tournamentapi.persistence.TournamentRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.MatchEntity;
import edu.fontysmaua.tournamentapi.persistence.entity.TeamEntity;
import edu.fontysmaua.tournamentapi.persistence.entity.TournamentEntity;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceImplTest {

    @Mock
    private MatchRepository matchRepository;
    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private MatchMapper matchMapper;

    @InjectMocks
    private MatchServiceImpl matchService;

    private MatchEntity matchEntity;
    private Match match;
    private TournamentEntity tournamentEntity;
    private TeamEntity team1Entity;
    private TeamEntity team2Entity;

    @BeforeEach
    void setUp() {
        tournamentEntity = new TournamentEntity();
        tournamentEntity.setId(1L);
        tournamentEntity.setName("Test Tournament");

        team1Entity = new TeamEntity();
        team1Entity.setId(1L);
        team1Entity.setName("Team 1");

        team2Entity = new TeamEntity();
        team2Entity.setId(2L);
        team2Entity.setName("Team 2");

        matchEntity = new MatchEntity();
        matchEntity.setId(1L);
        matchEntity.setRound(1);
        matchEntity.setTeam1Score(2);
        matchEntity.setTeam2Score(1);
        matchEntity.setStatus(Status.SCHEDULED);
        matchEntity.setTournament(tournamentEntity);
        matchEntity.setTeam1(team1Entity);
        matchEntity.setTeam2(team2Entity);

        match = new Match();
        match.setId(matchEntity.getId());
        match.setRound(matchEntity.getRound());
        match.setTeam1Score(matchEntity.getTeam1Score());
        match.setTeam2Score(matchEntity.getTeam2Score());
        match.setStatus(matchEntity.getStatus());
    }

    // --- findAll() tests ---

    @Test
    void findAll_ShouldReturnMappedMatches_WhenRepositoryReturnsEntities() {
        // Arrange
        when(matchRepository.findAll()).thenReturn(List.of(matchEntity));
        when(matchMapper.entitiesToModels(anyList())).thenReturn(List.of(match));

        // Act
        GetAllMatchesResponse response = matchService.findAll();

        // Assert
        assertNotNull(response);
        assertNotNull(response.getMatches());
        assertEquals(1, response.getMatches().size());
        assertEquals(match, response.getMatches().getFirst());

        verify(matchRepository, times(1)).findAll();
        verify(matchMapper, times(1)).entitiesToModels(anyList());
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenRepositoryReturnsEmpty() {
        // Arrange
        when(matchRepository.findAll()).thenReturn(Collections.emptyList());
        when(matchMapper.entitiesToModels(anyList())).thenReturn(Collections.emptyList());

        // Act
        GetAllMatchesResponse response = matchService.findAll();

        // Assert
        assertNotNull(response);
        assertTrue(response.getMatches().isEmpty());

        verify(matchRepository, times(1)).findAll();
        verify(matchMapper, times(1)).entitiesToModels(anyList());
    }

    // --- findById() tests ---

    @Test
    void findById_ShouldReturnMatch_WhenValidId() {
        // Arrange
        Long matchId = 1L;
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(matchEntity));
        when(matchMapper.entityToModel(any(MatchEntity.class))).thenReturn(match);

        // Act
        GetMatchByIdResponse response = matchService.findById(matchId);

        // Assert
        assertNotNull(response);
        assertEquals(match, response.getMatch());
        verify(matchRepository, times(1)).findById(matchId);
        verify(matchMapper, times(1)).entityToModel(matchEntity);
    }

    @Test
    void findById_ShouldThrowException_WhenIdIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> matchService.findById(null));
        assertEquals("Match ID cannot be null.", exception.getMessage());
        verify(matchRepository, never()).findById(any());
    }

    @Test
    void findById_ShouldThrowException_WhenIdIsZero() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> matchService.findById(0L));
        assertEquals("Match ID must be greater than 0.", exception.getMessage());
        verify(matchRepository, never()).findById(any());
    }

    @Test
    void findById_ShouldThrowException_WhenIdIsNegative() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> matchService.findById(-1L));
        assertEquals("Match ID must be greater than 0.", exception.getMessage());
        verify(matchRepository, never()).findById(any());
    }

    @Test
    void findById_ShouldThrowException_WhenMatchNotFound() {
        // Arrange
        Long matchId = 999L;
        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> matchService.findById(matchId));
        assertEquals("Match not found", exception.getMessage());
        verify(matchRepository, times(1)).findById(matchId);
    }

    // --- findAllUpcoming() tests ---

    @Test
    void findAllUpcoming_ShouldReturnMappedMatches_WhenRepositoryReturnsEntities() {
        // Arrange
        LocalDateTime fixedTime = LocalDateTime.of(2025, 1, 15, 10, 0);

        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(fixedTime);

            when(matchRepository.findAllByTournamentStartTime(fixedTime)).thenReturn(List.of(matchEntity));
            when(matchMapper.entitiesToModels(anyList())).thenReturn(List.of(match));

            // Act
            GetAllUpcomingMatchesResponse response = matchService.findAllUpcoming();

            // Assert
            assertNotNull(response);
            assertNotNull(response.getMatches());
            assertEquals(1, response.getMatches().size());
            assertEquals(match, response.getMatches().getFirst());

            verify(matchRepository, times(1)).findAllByTournamentStartTime(fixedTime);
            verify(matchMapper, times(1)).entitiesToModels(anyList());
        }
    }

    @Test
    void findAllUpcoming_ShouldReturnEmptyList_WhenNoUpcomingMatches() {
        // Arrange
        LocalDateTime fixedTime = LocalDateTime.of(2025, 1, 15, 10, 0);

        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(fixedTime);

            when(matchRepository.findAllByTournamentStartTime(fixedTime)).thenReturn(Collections.emptyList());
            when(matchMapper.entitiesToModels(anyList())).thenReturn(Collections.emptyList());

            // Act
            GetAllUpcomingMatchesResponse response = matchService.findAllUpcoming();

            // Assert
            assertNotNull(response);
            assertTrue(response.getMatches().isEmpty());

            verify(matchRepository, times(1)).findAllByTournamentStartTime(fixedTime);
            verify(matchMapper, times(1)).entitiesToModels(anyList());
        }
    }

    // --- create() tests ---

    @Test
    void create_ShouldCreateMatch_WhenDataIsValid() {
        // Arrange
        SaveMatchRequest request = new SaveMatchRequest();
        request.setTournamentId(1L);
        request.setTeam1Id(1L);
        request.setTeam2Id(2L);
        request.setRound(1);
        request.setTeam1Score(0);
        request.setTeam2Score(0);

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournamentEntity));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team1Entity));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(team2Entity));
        when(matchRepository.save(any(MatchEntity.class))).thenReturn(matchEntity);
        when(matchMapper.entityToModel(any(MatchEntity.class))).thenReturn(match);

        // Act
        SavedMatchResponse response = matchService.create(request);

        // Assert
        assertNotNull(response);
        assertEquals(match, response.getMatch());
        
        verify(tournamentRepository, times(1)).findById(1L);
        verify(teamRepository, times(1)).findById(1L);
        verify(teamRepository, times(1)).findById(2L);
        verify(matchRepository, times(1)).save(any(MatchEntity.class));
        verify(matchMapper, times(1)).entityToModel(any(MatchEntity.class));
    }

    @Test
    void create_ShouldThrowException_WhenTournamentDoesNotExist() {
        // Arrange
        SaveMatchRequest request = new SaveMatchRequest();
        request.setTournamentId(999L);
        request.setTeam1Id(1L);
        request.setTeam2Id(2L);

        when(tournamentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> matchService.create(request));
        assertEquals("Tournament not found", exception.getMessage());
        
        verify(tournamentRepository, times(1)).findById(999L);
        verify(teamRepository, never()).findById(any());
        verify(matchRepository, never()).save(any());
    }

    @Test
    void create_ShouldThrowException_WhenTeam1DoesNotExist() {
        // Arrange
        SaveMatchRequest request = new SaveMatchRequest();
        request.setTournamentId(1L);
        request.setTeam1Id(999L);
        request.setTeam2Id(2L);

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournamentEntity));
        when(teamRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> matchService.create(request));
        assertEquals("Team 1 not found", exception.getMessage());
        
        verify(tournamentRepository, times(1)).findById(1L);
        verify(teamRepository, times(1)).findById(999L);
        verify(matchRepository, never()).save(any());
    }

    @Test
    void create_ShouldThrowException_WhenTeam2DoesNotExist() {
        // Arrange
        SaveMatchRequest request = new SaveMatchRequest();
        request.setTournamentId(1L);
        request.setTeam1Id(1L);
        request.setTeam2Id(999L);

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournamentEntity));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team1Entity));
        when(teamRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> matchService.create(request));
        assertEquals("Team 2 not found", exception.getMessage());
        
        verify(tournamentRepository, times(1)).findById(1L);
        verify(teamRepository, times(1)).findById(1L);
        verify(teamRepository, times(1)).findById(999L);
        verify(matchRepository, never()).save(any());
    }

    // --- update() tests ---

    @Test
    void update_ShouldUpdateMatch_WhenValidRequest() {
        // Arrange
        SaveMatchRequest request = new SaveMatchRequest();
        request.setId(1L);
        request.setTournamentId(1L);
        request.setTeam1Id(1L);
        request.setTeam2Id(2L);
        request.setRound(2);
        request.setTeam1Score(3);
        request.setTeam2Score(2);

        when(matchRepository.existsById(1L)).thenReturn(true);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournamentEntity));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team1Entity));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(team2Entity));
        when(matchRepository.save(any(MatchEntity.class))).thenReturn(matchEntity);
        when(matchMapper.entityToModel(any(MatchEntity.class))).thenReturn(match);

        // Act
        SavedMatchResponse response = matchService.update(request);

        // Assert
        assertNotNull(response);
        assertEquals(match, response.getMatch());
        
        verify(matchRepository, times(1)).existsById(1L);
        verify(tournamentRepository, times(1)).findById(1L);
        verify(teamRepository, times(1)).findById(1L);
        verify(teamRepository, times(1)).findById(2L);
        verify(matchRepository, times(1)).save(any(MatchEntity.class));
        verify(matchMapper, times(1)).entityToModel(any(MatchEntity.class));
    }

    @Test
    void update_ShouldThrow_WhenIdIsNull() {
        // Arrange
        SaveMatchRequest request = new SaveMatchRequest();
        request.setId(null);
        request.setTournamentId(1L);
        request.setTeam1Id(1L);
        request.setTeam2Id(2L);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> matchService.update(request));
        assertEquals("Match ID cannot be null", exception.getMessage());
        
        verify(matchRepository, never()).existsById(any());
        verify(tournamentRepository, never()).findById(any());
        verify(matchRepository, never()).save(any());
    }

    @Test
    void update_ShouldThrow_WhenMatchDoesNotExist() {
        // Arrange
        SaveMatchRequest request = new SaveMatchRequest();
        request.setId(999L);
        request.setTournamentId(1L);
        request.setTeam1Id(1L);
        request.setTeam2Id(2L);

        when(matchRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> matchService.update(request));
        assertEquals("Match does not exist in the database", exception.getMessage());
        
        verify(matchRepository, times(1)).existsById(999L);
        verify(tournamentRepository, never()).findById(any());
        verify(matchRepository, never()).save(any());
    }

    @Test
    void update_ShouldThrow_WhenTournamentNotFound() {
        // Arrange
        SaveMatchRequest request = new SaveMatchRequest();
        request.setId(1L);
        request.setTournamentId(999L);
        request.setTeam1Id(1L);
        request.setTeam2Id(2L);

        when(matchRepository.existsById(1L)).thenReturn(true);
        when(tournamentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> matchService.update(request));
        assertEquals("Tournament not found", exception.getMessage());
        
        verify(matchRepository, times(1)).existsById(1L);
        verify(tournamentRepository, times(1)).findById(999L);
        verify(teamRepository, never()).findById(any());
        verify(matchRepository, never()).save(any());
    }

    @Test
    void update_ShouldThrow_WhenTeam1NotFound() {
        // Arrange
        SaveMatchRequest request = new SaveMatchRequest();
        request.setId(1L);
        request.setTournamentId(1L);
        request.setTeam1Id(999L);
        request.setTeam2Id(2L);

        when(matchRepository.existsById(1L)).thenReturn(true);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournamentEntity));
        when(teamRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> matchService.update(request));
        assertEquals("Team 1 not found", exception.getMessage());
        
        verify(matchRepository, times(1)).existsById(1L);
        verify(tournamentRepository, times(1)).findById(1L);
        verify(teamRepository, times(1)).findById(999L);
        verify(matchRepository, never()).save(any());
    }

    @Test
    void update_ShouldThrow_WhenTeam2NotFound() {
        // Arrange
        SaveMatchRequest request = new SaveMatchRequest();
        request.setId(1L);
        request.setTournamentId(1L);
        request.setTeam1Id(1L);
        request.setTeam2Id(999L);

        when(matchRepository.existsById(1L)).thenReturn(true);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournamentEntity));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team1Entity));
        when(teamRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> matchService.update(request));
        assertEquals("Team 2 not found", exception.getMessage());
        
        verify(matchRepository, times(1)).existsById(1L);
        verify(tournamentRepository, times(1)).findById(1L);
        verify(teamRepository, times(1)).findById(1L);
        verify(teamRepository, times(1)).findById(999L);
        verify(matchRepository, never()).save(any());
    }

    // --- cancelMatch() tests ---

    @Test
    void cancelMatch_ShouldCancelSuccessfully_WhenMatchIsScheduled() {
        // Arrange
        Long matchId = 1L;
        matchEntity.setStatus(Status.SCHEDULED);
        matchEntity.setTeam1Score(0);
        matchEntity.setTeam2Score(0);
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(matchEntity));
        when(matchRepository.save(any(MatchEntity.class))).thenReturn(matchEntity);

        // Act
        Long cancelledMatchId = matchService.cancelMatch(matchId);

        // Assert
        assertEquals(matchId, cancelledMatchId);
        assertEquals(Status.CANCELLED, matchEntity.getStatus());
        
        verify(matchRepository, times(1)).findById(matchId);
        verify(matchRepository, times(1)).save(matchEntity);
    }

    @Test
    void cancelMatch_ShouldCancelSuccessfully_WhenMatchIsPending() {
        // Arrange
        Long matchId = 1L;
        matchEntity.setStatus(Status.PENDING);
        matchEntity.setTeam1Score(0);
        matchEntity.setTeam2Score(0);
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(matchEntity));
        when(matchRepository.save(any(MatchEntity.class))).thenReturn(matchEntity);

        // Act
        Long cancelledMatchId = matchService.cancelMatch(matchId);

        // Assert
        assertEquals(matchId, cancelledMatchId);
        assertEquals(Status.CANCELLED, matchEntity.getStatus());
        
        verify(matchRepository, times(1)).findById(matchId);
        verify(matchRepository, times(1)).save(matchEntity);
    }

    @Test
    void cancelMatch_ShouldThrowException_WhenMatchNotFound() {
        // Arrange
        Long matchId = 999L;
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> matchService.cancelMatch(matchId));
        
        assertEquals("Match not found", exception.getMessage());
        
        verify(matchRepository, times(1)).findById(matchId);
        verify(matchRepository, never()).save(any());
    }

    @Test
    void cancelMatch_ShouldThrowException_WhenMatchIsAlreadyCancelled() {
        // Arrange
        Long matchId = 1L;
        matchEntity.setStatus(Status.CANCELLED);
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(matchEntity));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> matchService.cancelMatch(matchId));
        
        assertEquals("Match is already cancelled!", exception.getMessage());
        
        verify(matchRepository, times(1)).findById(matchId);
        verify(matchRepository, never()).save(any());
    }

    @Test
    void cancelMatch_ShouldThrowException_WhenMatchIsCompleted() {
        // Arrange
        Long matchId = 1L;
        matchEntity.setStatus(Status.COMPLETED);
        matchEntity.setTeam1Score(2);
        matchEntity.setTeam2Score(1);
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(matchEntity));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> matchService.cancelMatch(matchId));
        
        assertEquals("Cannot cancel a match with scores already set", exception.getMessage());
        
        verify(matchRepository, times(1)).findById(matchId);
        verify(matchRepository, never()).save(any());
    }
}
