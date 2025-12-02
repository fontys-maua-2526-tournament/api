package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.Match;
import edu.fontysmaua.tournamentapi.domain.request.SaveMatchRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllMatchesResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetAllUpcomingMatchesResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetMatchByIdResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedMatchResponse;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceImplTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private MatchMapper matchMapper;

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private MatchServiceImpl matchService;

    private MatchEntity matchEntity;
    private Match match;

    private TournamentEntity tournament;
    private TeamEntity team1;
    private TeamEntity team2;

    private SaveMatchRequest saveMatchRequest;

    @BeforeEach
    void setUp() {
        matchEntity = new MatchEntity();
        matchEntity.setId(1L);
        matchEntity.setRound(1);
        matchEntity.setTeam1Score(2);
        matchEntity.setTeam2Score(1);

        match = new Match();
        match.setId(matchEntity.getId());
        match.setRound(matchEntity.getRound());
        match.setTeam1Score(matchEntity.getTeam1Score());
        match.setTeam2Score(matchEntity.getTeam2Score());
        
        tournament = new TournamentEntity();
        tournament.setId(10L);

        team1 = new TeamEntity();
        team1.setId(100L);
        
        team2 = new TeamEntity();
        team2.setId(200L);

        saveMatchRequest = SaveMatchRequest.builder()
            .round(1)
            .tournamentId(10L)
            .team1Id(100L)
            .team2Id(200L)
            .team1Score(2)
            .team2Score(1)
            .build();
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
        //

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
    void findById_ShouldReturnMatch_WhenIdIsValid() {
        when(matchRepository.findById(1L)).thenReturn(Optional.of(matchEntity));
        when(matchMapper.entityToModel(matchEntity)).thenReturn(match);

        GetMatchByIdResponse response = matchService.findById(1L);

        assertNotNull(response);
        assertEquals(match, response.getMatch());

        verify(matchRepository).findById(1L);
        verify(matchMapper).entityToModel(matchEntity);
    }

    @Test
    void findById_ShouldThrowException_WhenIdIsNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> matchService.findById(null)
        );
        assertEquals("Match ID cannot be null.", ex.getMessage());

        verify(matchRepository, never()).findById(any());
    }

    @Test
    void findById_ShouldThrowException_WhenIdIsZero() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> matchService.findById(0L)
        );
        assertEquals("Match ID must be greater than 0.", ex.getMessage());

        verify(matchRepository, never()).findById(any());
    }

    @Test
    void findById_ShouldThrowException_WhenIdIsNegative() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> matchService.findById(-1L)
        );
        assertEquals("Match ID must be greater than 0.", ex.getMessage());

        verify(matchRepository, never()).findById(any());
    }

    @Test
    void findById_ShouldThrowException_WhenMatchNotFound() {
        when(matchRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> matchService.findById(999L)
        );
        assertEquals("Match not found", ex.getMessage());

        verify(matchRepository).findById(999L);
        verify(matchMapper, never()).entityToModel(any());
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


    /* --- SUCCESS CASE --- */
    @Test
    void create_ShouldCreateMatch_WhenDataIsValid() {
        MatchEntity savedEntity = new MatchEntity();
        savedEntity.setId(1L);
        savedEntity.setRound(1);
        savedEntity.setTournament(tournament);
        savedEntity.setTeam1(team1);
        savedEntity.setTeam2(team2);
        savedEntity.setTeam1Score(2);
        savedEntity.setTeam2Score(1);

        Match savedMatch = Match.builder()
            .id(1L)
            .round(1)
            .team1Score(2)
            .team2Score(1)
            .build();

        when(tournamentRepository.findById(10L)).thenReturn(java.util.Optional.of(tournament));
        when(teamRepository.findById(100L)).thenReturn(java.util.Optional.of(team1));
        when(teamRepository.findById(200L)).thenReturn(java.util.Optional.of(team2));
        when(matchRepository.save(any(MatchEntity.class))).thenReturn(savedEntity);
        when(matchMapper.entityToModel(savedEntity)).thenReturn(savedMatch);

        SavedMatchResponse response = matchService.create(saveMatchRequest);

        assertNotNull(response);
        assertEquals(savedMatch, response.getMatch());

        ArgumentCaptor<MatchEntity> entityCaptor = ArgumentCaptor.forClass(MatchEntity.class);
        verify(matchRepository).save(entityCaptor.capture());
        MatchEntity captured = entityCaptor.getValue();

        assertEquals(1, captured.getRound());
        assertEquals(team1, captured.getTeam1());
        assertEquals(team2, captured.getTeam2());
        assertEquals(tournament, captured.getTournament());

        verify(matchMapper).entityToModel(savedEntity);
    }

    /* --- TOURNAMENT DOES NOT EXIST --- */
    @Test
    void create_ShouldThrowException_WhenTournamentDoesNotExist() {
        when(tournamentRepository.findById(10L)).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class,
            () -> matchService.create(saveMatchRequest));

        verify(matchRepository, never()).save(any());
    }

    /* --- TEAM 1 DOES NOT EXIST --- */
    @Test
    void create_ShouldThrowException_WhenTeam1DoesNotExist() {
        when(tournamentRepository.findById(10L)).thenReturn(java.util.Optional.of(tournament));
        when(teamRepository.findById(100L)).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class,
            () -> matchService.create(saveMatchRequest));

        verify(matchRepository, never()).save(any());
    }

    /* --- TEAM 2 DOES NOT EXIST --- */
    @Test
    void create_ShouldThrowException_WhenTeam2DoesNotExist() {
        when(tournamentRepository.findById(10L)).thenReturn(java.util.Optional.of(tournament));
        when(teamRepository.findById(100L)).thenReturn(java.util.Optional.of(team1));
        when(teamRepository.findById(200L)).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class,
            () -> matchService.create(saveMatchRequest));

        verify(matchRepository, never()).save(any());
    }

    // --- update() tests ---
    @Test
    void update_ShouldUpdateMatch_WhenValidRequest() {
        SaveMatchRequest request = SaveMatchRequest.builder()
                .id(1L)
                .round(3)
                .tournamentId(10L)
                .team1Id(100L)
                .team2Id(200L)
                .team1Score(5)
                .team2Score(6)
                .build();

        MatchEntity updatedEntity = MatchEntity.builder()
                .id(1L)
                .round(3)
                .tournament(tournament)
                .team1(team1)
                .team2(team2)
                .team1Score(5)
                .team2Score(6)
                .build();

        Match updatedMatch = Match.builder()
                .id(1L)
                .round(3)
                .team1Score(5)
                .team2Score(6)
                .build();

        when(matchRepository.existsById(1L)).thenReturn(true);
        when(tournamentRepository.findById(10L)).thenReturn(Optional.of(tournament));
        when(teamRepository.findById(100L)).thenReturn(Optional.of(team1));
        when(teamRepository.findById(200L)).thenReturn(Optional.of(team2));
        when(matchRepository.save(any())).thenReturn(updatedEntity);
        when(matchMapper.entityToModel(updatedEntity)).thenReturn(updatedMatch);

        SavedMatchResponse response = matchService.update(request);

        assertNotNull(response);
        assertEquals(updatedMatch, response.getMatch());

        verify(matchRepository).existsById(1L);
        verify(matchRepository).save(any());
        verify(matchMapper).entityToModel(updatedEntity);
    }

    @Test
    void update_ShouldThrow_WhenIdIsNull() {
        SaveMatchRequest request = SaveMatchRequest.builder()
                .id(null)
                .tournamentId(10L)
                .team1Id(100L)
                .team2Id(200L)
                .build();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> matchService.update(request)
        );

        assertEquals("Match ID cannot be null", ex.getMessage());
    }

    @Test
    void update_ShouldThrow_WhenMatchDoesNotExist() {
        SaveMatchRequest request = SaveMatchRequest.builder()
                .id(999L)
                .tournamentId(10L)
                .team1Id(100L)
                .team2Id(200L)
                .build();

        when(matchRepository.existsById(999L)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> matchService.update(request)
        );

        assertEquals("Match does not exist in the database", ex.getMessage());
    }

    @Test
    void update_ShouldThrow_WhenTournamentNotFound() {
        SaveMatchRequest request = SaveMatchRequest.builder()
                .id(1L)
                .tournamentId(10L)
                .team1Id(100L)
                .team2Id(200L)
                .build();

        when(matchRepository.existsById(1L)).thenReturn(true);
        when(tournamentRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> matchService.update(request));
    }

    @Test
    void update_ShouldThrow_WhenTeam1NotFound() {
        SaveMatchRequest request = SaveMatchRequest.builder()
                .id(1L)
                .tournamentId(10L)
                .team1Id(100L)
                .team2Id(200L)
                .build();

        when(matchRepository.existsById(1L)).thenReturn(true);
        when(tournamentRepository.findById(10L)).thenReturn(Optional.of(tournament));
        when(teamRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> matchService.update(request));
    }

    @Test
    void update_ShouldThrow_WhenTeam2NotFound() {
        SaveMatchRequest request = SaveMatchRequest.builder()
                .id(1L)
                .tournamentId(10L)
                .team1Id(100L)
                .team2Id(200L)
                .build();

        when(matchRepository.existsById(1L)).thenReturn(true);
        when(tournamentRepository.findById(10L)).thenReturn(Optional.of(tournament));
        when(teamRepository.findById(100L)).thenReturn(Optional.of(team1));
        when(teamRepository.findById(200L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> matchService.update(request));
    }
}
