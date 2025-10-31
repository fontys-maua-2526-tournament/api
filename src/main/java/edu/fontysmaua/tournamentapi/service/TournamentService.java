package edu.fontysmaua.tournamentapi.service;

import edu.fontysmaua.tournamentapi.domain.request.SaveTournamentRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllTournamentsResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetTournamentByIdResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedTournamentResponse;


public interface TournamentService {
    GetAllTournamentsResponse findAll();

    GetTournamentByIdResponse findById(Long id);

    SavedTournamentResponse create(SaveTournamentRequest request);

    SavedTournamentResponse update(SaveTournamentRequest request);

    Long delete(Long tournamentId);
}
