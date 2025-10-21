package edu.fontysmaua.tournamentapi.business;

import edu.fontysmaua.tournamentapi.business.impl.DeleteTournamentUseCaseImpl;
import edu.fontysmaua.tournamentapi.persistence.TournamentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteTournamentUseCaseTest {
    @Mock
    private TournamentRepository tournamentRepository;
    @InjectMocks
    private DeleteTournamentUseCaseImpl deleteTournamentUseCase;

    @Test
    void delete_WithValidId_ShouldDeleteTournamentAndReturnId() {
        // Arrange
        Long tournamentId = 1L;

        // Act
        Long result = deleteTournamentUseCase.delete(tournamentId);

        // Assert
        assertEquals(tournamentId, result);
        verify(tournamentRepository, times(1)).deleteById(tournamentId);
    }

    @Test
    void delete_WithLargeValidId_ShouldDeleteTournamentAndReturnId() {
        // Arrange
        Long tournamentId = 999L;

        // Act
        Long result = deleteTournamentUseCase.delete(tournamentId);

        // Assert
        assertEquals(tournamentId, result);
        verify(tournamentRepository, times(1)).deleteById(tournamentId);
    }

    @Test
    void delete_WithNullId_ShouldThrowIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> deleteTournamentUseCase.delete(null)
        );

        assertEquals("ID cannot be null.", exception.getMessage());
        verify(tournamentRepository, never()).deleteById(any());
    }

    @Test
    void delete_WithZeroId_ShouldThrowIllegalArgumentException() {
        // Arrange
        Long tournamentId = 0L;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> deleteTournamentUseCase.delete(tournamentId)
        );

        assertEquals("ID must be greater than 0.", exception.getMessage());
        verify(tournamentRepository, never()).deleteById(any());
    }

    @Test
    void delete_WithNegativeId_ShouldThrowIllegalArgumentException() {
        // Arrange
        Long tournamentId = -1L;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> deleteTournamentUseCase.delete(tournamentId)
        );

        assertEquals("ID must be greater than 0.", exception.getMessage());
        verify(tournamentRepository, never()).deleteById(any());
    }

    @Test
    void delete_WithLargeNegativeId_ShouldThrowIllegalArgumentException() {
        // Arrange
        Long tournamentId = -999L;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> deleteTournamentUseCase.delete(tournamentId)
        );

        assertEquals("ID must be greater than 0.", exception.getMessage());
        verify(tournamentRepository, never()).deleteById(any());
    }
}