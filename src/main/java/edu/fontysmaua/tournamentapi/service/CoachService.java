package edu.fontysmaua.tournamentapi.service;

import edu.fontysmaua.tournamentapi.domain.request.AddAthleteToTeamRequest;
import edu.fontysmaua.tournamentapi.domain.request.SaveCoachRequest;
import edu.fontysmaua.tournamentapi.domain.request.UpdateTeamRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllCoachesResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetTournamentsByUserIdResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedCoachResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedTeamResponse;
import edu.fontysmaua.tournamentapi.domain.response.TeamMemberResponse;

public interface CoachService {
    GetAllCoachesResponse findAll();

    GetTournamentsByUserIdResponse findTournamentsByUserId(Long userId);

    SavedCoachResponse create(SaveCoachRequest request);

    SavedCoachResponse update(SaveCoachRequest request);

    Long delete(Long id);

    void disbandTeam(Long teamId);
    
    void registerTeamInTournament(Long teamId, Long tournamentId);

    void withdrawTeamFromTournament(Long teamId, Long tournamentId);

    // New use cases
    TeamMemberResponse addUnderageAthleteToTeam(AddAthleteToTeamRequest request, Long coachId);

    SavedTeamResponse updateTeam(UpdateTeamRequest request, Long coachId);
}

