package edu.fontysmaua.tournamentapi.business.impl;

import edu.fontysmaua.tournamentapi.exception.NameAlreadyExistsException;
import edu.fontysmaua.tournamentapi.domain.request.SaveTournamentRequest;
import edu.fontysmaua.tournamentapi.domain.response.CreateTournamentResponse;
import edu.fontysmaua.tournamentapi.persistence.TournamentRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.TournamentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTournamentUseCaseImplTest {
    @Mock
    private TournamentRepository repository;

    @InjectMocks
    private CreateTournamentUseCaseImpl createTournamentUseCase;

    private SaveTournamentRequest request;
    private TournamentEntity savedTournament;

    @BeforeEach
    void setUp() {
        request = SaveTournamentRequest.builder()
                .name("Tournament")
                .address("Fontys Rachelsmolen")
                .startTime(LocalDateTime.of(LocalDate.of(2026,1,29), LocalTime.of(9, 0)))
                .endTime(LocalDateTime.of(LocalDate.of(2026,1,30), LocalTime.of(9, 0)))
                .build();

        savedTournament = TournamentEntity.builder()
                .id(1L)
                .name("Tournament")
                .address("Fontys Rachelsmolen")
                .startTime(LocalDateTime.of(LocalDate.of(2026,1,29), LocalTime.of(9, 0)))
                .endTime(LocalDateTime.of(LocalDate.of(2026,1,30), LocalTime.of(9, 0)))
                .build();
    }

    @Test
    void createTournament_ShouldSaveTournament() {
        // Arrange
        when(repository.existsByName(request.getName())).thenReturn(false);
        when(repository.save(any(TournamentEntity.class))).thenReturn(savedTournament);

        // Act
        CreateTournamentResponse result = createTournamentUseCase.createTournament(request);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(repository, times(1)).existsByName(request.getName());
        verify(repository, times(1)).save(any(TournamentEntity.class));
    }

    @Test
    void createTournament_WhenNameExists_ShouldThrowNameAlreadyExistsException() {
        // Arrange
        when(repository.existsByName(request.getName())).thenReturn(true);

        // Act
        assertThrows(NameAlreadyExistsException.class,
                () -> createTournamentUseCase.createTournament(request));

        // Assert
        verify(repository, times(1)).existsByName(request.getName());
        verify(repository, never()).save(any(TournamentEntity.class));
    }
}