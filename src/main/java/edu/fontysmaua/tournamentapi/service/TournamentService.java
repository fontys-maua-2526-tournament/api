package edu.fontysmaua.tournamentapi.service;

import edu.fontysmaua.tournamentapi.domain.request.AddTeamToTournament;
import edu.fontysmaua.tournamentapi.domain.request.RemoveTeamFromTournamentRequest;
import edu.fontysmaua.tournamentapi.domain.request.SaveTournamentRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllTournamentsResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetTournamentByIdResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetTournamentsByUserIdResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedTournamentResponse;
import jakarta.validation.constraints.Positive;

public interface TournamentService {
    GetAllTournamentsResponse findAll();

    GetTournamentByIdResponse findById(Long id);

    GetTournamentsByUserIdResponse getByUserId(Long userId);

    SavedTournamentResponse create(SaveTournamentRequest request);

    SavedTournamentResponse update(SaveTournamentRequest request);

    Boolean addTeam(AddTeamToTournament request);

    Boolean removeTeam(RemoveTeamFromTournamentRequest request);

    Long cancel(Long tournamentId);
}
