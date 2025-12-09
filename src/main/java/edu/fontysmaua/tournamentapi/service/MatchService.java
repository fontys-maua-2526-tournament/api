package edu.fontysmaua.tournamentapi.service;

import edu.fontysmaua.tournamentapi.domain.request.SaveMatchRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllMatchesResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetMatchByIdResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetAllUpcomingMatchesResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedMatchResponse;

public interface MatchService {
    GetAllMatchesResponse findAll();

    GetMatchByIdResponse findById(Long id);

    GetAllUpcomingMatchesResponse findAllUpcoming();

    SavedMatchResponse create(SaveMatchRequest request);

    SavedMatchResponse update(SaveMatchRequest request);
  
    Long cancelMatch(Long matchId);
}
