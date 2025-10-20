package edu.fontysmaua.tournamentapi.business;

import edu.fontysmaua.tournamentapi.domain.dto.tournament.CreateTournamentRequest;
import edu.fontysmaua.tournamentapi.domain.dto.tournament.CreateTournamentResponse;

public interface CreateTournamentUseCase {
    CreateTournamentResponse createTournament(CreateTournamentRequest request);
}
