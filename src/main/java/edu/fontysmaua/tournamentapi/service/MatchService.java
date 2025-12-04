package edu.fontysmaua.tournamentapi.service;

import edu.fontysmaua.tournamentapi.domain.response.GetAllMatchesResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetAllUpcomingMatchesResponse;

public interface MatchService {
    GetAllMatchesResponse findAll();

    GetAllUpcomingMatchesResponse findAllUpcoming();

    Long cancelMatch(Long matchId);
}
