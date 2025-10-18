package edu.fontysmaua.tournamentapi.business.impl.Team;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import edu.fontysmaua.tournamentapi.business.TeamUseCases;
import edu.fontysmaua.tournamentapi.domain.Team.GetAllTeamsResponse;
import edu.fontysmaua.tournamentapi.domain.Team.Team;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GetAllTeamsImpl implements TeamUseCases.getAllTeams {

  @Override
  public GetAllTeamsResponse getAllTeams() {
    List<Team> teams = findAll().stream().map(TeamConverter::convert).toList();
    return GetAllTeamsResponse.builder().teams(teams).build();
  }

  public List<Team> findAll() {
    return Collections.emptyList();
  }
}
