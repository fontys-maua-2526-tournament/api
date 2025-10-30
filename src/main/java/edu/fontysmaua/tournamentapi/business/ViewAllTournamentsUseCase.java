package edu.fontysmaua.tournamentapi.business;

import edu.fontysmaua.tournamentapi.domain.Tournament;
import java.util.List;

public interface ViewAllTournamentsUseCase {
    List<Tournament> viewAllTournaments();
}
