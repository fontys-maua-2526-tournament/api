package edu.fontysmaua.tournamentapi.service;

import edu.fontysmaua.tournamentapi.domain.Team.Team;
import edu.fontysmaua.tournamentapi.domain.Team.request.SaveTeamRequest;
import edu.fontysmaua.tournamentapi.domain.Team.response.SavedTeamResponse;

public interface TeamService {
    SavedTeamResponse createTeam(SaveTeamRequest request);

    SavedTeamResponse updateTeam(SaveTeamRequest request);
}
