package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.Match;
import edu.fontysmaua.tournamentapi.domain.response.GetAllMatchesResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetAllUpcomingMatchesResponse;
import edu.fontysmaua.tournamentapi.enums.Status;
import edu.fontysmaua.tournamentapi.mapper.MatchMapper;
import edu.fontysmaua.tournamentapi.persistence.MatchRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.MatchEntity;
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
    private MatchMapper matchMapper;

    @InjectMocks
    private MatchServiceImpl matchService;

    private MatchEntity matchEntity;
    private Match match;

    @BeforeEach
    void setUp() {
        matchEntity = new MatchEntity();
        matchEntity.setId(1L);
        matchEntity.setRound(1);
        matchEntity.setTeam1Score(2);
        matchEntity.setTeam2Score(1);
        matchEntity.setStatus(Status.SCHEDULED);

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
        matchEntity.setTeam1Score(-1);
        matchEntity.setTeam2Score(-1);
        
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
