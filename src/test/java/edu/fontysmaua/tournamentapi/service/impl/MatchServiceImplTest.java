package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.Match;
import edu.fontysmaua.tournamentapi.domain.response.GetAllMatchesResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetAllUpcomingMatchesResponse;
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

import static org.junit.jupiter.api.Assertions.*;
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

        match = new Match();
        match.setId(matchEntity.getId());
        match.setRound(matchEntity.getRound());
        match.setTeam1Score(matchEntity.getTeam1Score());
        match.setTeam2Score(matchEntity.getTeam2Score());
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
}
