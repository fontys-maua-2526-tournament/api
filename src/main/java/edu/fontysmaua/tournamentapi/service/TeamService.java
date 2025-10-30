package edu.fontysmaua.tournamentapi.service;

import edu.fontysmaua.tournamentapi.domain.request.SaveTeamRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllTeamsResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedTeamResponse;

public interface TeamService {
    GetAllTeamsResponse findAll();

    SavedTeamResponse create(SaveTeamRequest request);

    SavedTeamResponse update(SaveTeamRequest request);

    Long delete(Long teamId);
}
