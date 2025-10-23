package edu.fontysmaua.tournamentapi.service;

import edu.fontysmaua.tournamentapi.domain.Team.response.GetAllTeamsResponse;

public interface TeamService {
    Long delete(Long teamId);
    GetAllTeamsResponse getAll();
}
