package edu.fontysmaua.tournamentapi.business;

import edu.fontysmaua.tournamentapi.domain.Tournament;

public interface TournamentUpdateUseCase {
    Tournament updateTournament(Tournament tournament);
}
