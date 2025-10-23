package edu.fontysmaua.tournamentapi.business;

import edu.fontysmaua.tournamentapi.domain.Team.response.CreateTeamResponse;
import edu.fontysmaua.tournamentapi.domain.Team.response.DeleteTeamResponse;
import edu.fontysmaua.tournamentapi.domain.Team.response.GetAllTeamsResponse;
import edu.fontysmaua.tournamentapi.domain.Team.response.UpdateTeamResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.fontysmaua.tournamentapi.persistence.TeamRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.TeamEntity;

@Service
public class TeamUseCases {

  // create - save in the database
  @Autowired
  private TeamRepository teamRepository;

  public void createTeam(TeamEntity team) {
    this.teamRepository.save(team);
  }

  public interface getAllTeams {
    GetAllTeamsResponse getAllTeams();
  }

  public interface updateTeam {
    UpdateTeamResponse updateTeam(TeamEntity team);
  }
  public interface DeleteTeam {
      DeleteTeamResponse deleteTeamById(String teamId);
  }
  public interface CreateTeam {
    CreateTeamResponse createTeam(TeamEntity team);
  }
}
