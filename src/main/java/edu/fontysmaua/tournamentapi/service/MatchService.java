package edu.fontysmaua.tournamentapi.service;

import edu.fontysmaua.tournamentapi.domain.request.SaveMatchRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllMatchesResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetAllUpcomingMatchesResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedMatchResponse;

public interface MatchService {
    GetAllMatchesResponse findAll();

    GetAllUpcomingMatchesResponse findAllUpcoming();

    SavedMatchResponse create(SaveMatchRequest request);
}
