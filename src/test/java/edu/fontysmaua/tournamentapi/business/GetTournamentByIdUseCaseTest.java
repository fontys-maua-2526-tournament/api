package edu.fontysmaua.tournamentapi.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.fontysmaua.tournamentapi.business.impl.GetTournamentByIdUseCaseImpl;
import edu.fontysmaua.tournamentapi.domain.Tournament;
import edu.fontysmaua.tournamentapi.persistence.TournamentRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.TournamentEntity;

@ExtendWith(MockitoExtension.class)
public class GetTournamentByIdUseCaseTest {
    
    @Mock
    private TournamentRepository tournamentRepository;

    @InjectMocks
    private GetTournamentByIdUseCaseImpl getTournamentByIdUseCase;

    @Test
    void getById_WithValidId_ShouldReturnTournament(){
        Long tournamentId = 1L;
        TournamentEntity entity = TournamentEntity.builder()
            .id(tournamentId)
            .name("Test Tournament")
            .address("Test Address")
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusHours(2))
            .build();

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(entity));
    
        Tournament result = getTournamentByIdUseCase.getTournamentById(tournamentId);

        assertNotNull(result);
        assertEquals(tournamentId, result.getId());
        assertEquals(entity.getName(), result.getName());
        assertEquals(entity.getAddress(), result.getAddress());
        assertEquals(entity.getStartTime(), result.getStartTime());
        assertEquals(entity.getEndTime(), result.getEndTime());
        verify(tournamentRepository, times(1)).findById(tournamentId);
    }

    @Test
    void getById_WithLargeValidId_ShouldReturnTournament(){

        Long tournamentId = 999L;
        TournamentEntity entity = TournamentEntity.builder()
            .id(tournamentId)
            .name("Large ID Tournament")
            .address("Test Address")
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusHours(3))
            .build();

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(entity));
    
        Tournament result = getTournamentByIdUseCase.getTournamentById(tournamentId);
    
        assertNotNull(result);
        assertEquals(tournamentId, result.getId());
        assertEquals(entity.getName(), result.getName());
        verify(tournamentRepository, times(1)).findById(tournamentId);
    }

    @Test
    void getById_WithNullId_ShouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> getTournamentByIdUseCase.getTournamentById(null)
        );

        assertEquals("ID cannot be null", exception.getMessage());
        verify(tournamentRepository, never()).findById(any());
    }

    @Test
    void getById_WithZeroId_ShouldThrowIllegalArgumentException() {
        Long tournamentId = 0L;

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> getTournamentByIdUseCase.getTournamentById(tournamentId)
        );

        assertEquals("ID must be greater than 0", exception.getMessage());
        verify(tournamentRepository, never()).findById(any());
    }

    @Test
    void getById_WithNegativeId_ShouldThrowIllegalArgumentException() {
        Long tournamentId = -1L;

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> getTournamentByIdUseCase.getTournamentById(tournamentId)
        );

        assertEquals("ID must be greater than 0", exception.getMessage());
        verify(tournamentRepository, never()).findById(any());
    }

    @Test
    void getById_WithLargeNegativeId_ShouldThrowIllegalArgumentException() {
        Long tournamentId = -999L;

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> getTournamentByIdUseCase.getTournamentById(tournamentId)
        );

        assertEquals("ID must be greater than 0", exception.getMessage());
        verify(tournamentRepository, never()).findById(any());
    }

    @Test
    void getById_WithNonExistentId_ShouldThrowIllegalArgumentException() {
        Long tournamentId = 999L;
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> getTournamentByIdUseCase.getTournamentById(tournamentId)
        );

        assertEquals("Tournament not found", exception.getMessage());
        verify(tournamentRepository, times(1)).findById(tournamentId);
    }
}

