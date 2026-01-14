package edu.fontysmaua.tournamentapi.service;

import edu.fontysmaua.tournamentapi.domain.request.JoinTeamRequest;
import edu.fontysmaua.tournamentapi.domain.response.TeamMemberResponse;

public interface AthleteService {
    /**
     * Allows an adult athlete to join a team using an invite code.
     * Underage athletes must be added by their coach directly.
     */
    TeamMemberResponse joinTeamViaInvite(JoinTeamRequest request);
}

