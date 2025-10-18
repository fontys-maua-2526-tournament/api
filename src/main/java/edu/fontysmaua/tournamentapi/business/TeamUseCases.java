package edu.fontysmaua.tournamentapi.business;

import edu.fontysmaua.tournamentapi.domain.Team.GetAllTeamsResponse;

public interface TeamUseCases {

  
  // GET
  public interface getAllTeams {
    GetAllTeamsResponse getAllTeams();
  }
}
