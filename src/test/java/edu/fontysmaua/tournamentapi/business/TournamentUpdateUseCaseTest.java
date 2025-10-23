package edu.fontysmaua.tournamentapi.business;

import edu.fontysmaua.tournamentapi.business.impl.TournamentUpdateUseCaseImpl;
import edu.fontysmaua.tournamentapi.domain.Tournament;
import edu.fontysmaua.tournamentapi.persistence.TournamentRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.TournamentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentUpdateUseCaseTest {
    @Mock
    private TournamentRepository tournamentRepo;

    @InjectMocks
    private TournamentUpdateUseCaseImpl tournamentUpdate;

    private TournamentEntity tournamentEntity;
    private Tournament tournament;


    @BeforeEach
    void setUp() {
        tournamentEntity = new TournamentEntity();
        tournamentEntity.setId(1L);
        tournamentEntity.setName("Tournament");

        tournament = new Tournament();
        tournament.setId(tournamentEntity.getId());
        tournament.setName(tournamentEntity.getName());
    }

    @Test
    void updateTournament_fail() {
        assertThrows(IllegalArgumentException.class, () -> tournamentUpdate.updateTournament(tournament));
        verify(tournamentRepo, times(1)).existsById(any(Long.class));
        verify(tournamentRepo, times(0)).save(any(TournamentEntity.class));
    }

    @Test
    void updateTournament_success() {
        when(tournamentRepo.existsById(any(Long.class))).thenReturn(true);
        when(tournamentRepo.save(any(TournamentEntity.class))).thenReturn(tournamentEntity);

        assertDoesNotThrow(() -> tournamentUpdate.updateTournament(tournament));
        verify(tournamentRepo, times(1)).existsById(any(Long.class));
        verify(tournamentRepo, times(1)).save(any(TournamentEntity.class));
    }
}