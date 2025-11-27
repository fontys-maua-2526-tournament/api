package edu.fontysmaua.tournamentapi.service;

import edu.fontysmaua.tournamentapi.domain.request.SaveCoachRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllCoachesResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetTournamentsByUserIdResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedCoachResponse;

public interface CoachService {
    GetAllCoachesResponse findAll();

    GetTournamentsByUserIdResponse findTournamentsByUserId(Long userId);

    SavedCoachResponse create(SaveCoachRequest request);

    SavedCoachResponse update(SaveCoachRequest request);

    Long delete(Long id);

    void disbandTeam(Long teamId);

    void registerTeamInTournament(Long teamId, Long tournamentId);

    void withdrawTeamFromTournament(Long teamId, Long tournamentId);
}
