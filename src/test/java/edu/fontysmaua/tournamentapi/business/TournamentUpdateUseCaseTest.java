package edu.fontysmaua.tournamentapi.business;

import edu.fontysmaua.tournamentapi.business.impl.TournamentUpdateUseCaseImpl;
import edu.fontysmaua.tournamentapi.domain.Tournament;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class TournamentUpdateUseCaseTest {
    @Mock
    private TournamentUpdateUseCaseImpl tournamentUpdate;

    @Test
    void updateTournament_fail() {
        Tournament tournament = new Tournament();

        assertThrows(IllegalArgumentException.class, () -> tournamentUpdate.updateTournament(tournament));
    }
}