package edu.fontysmaua.tournamentapi.business.impl.Team;

import edu.fontysmaua.tournamentapi.domain.Team.Team;

public class TeamConverter {
  public static Team convert(Team team) {
    return Team.builder()
        .id(team.getId())
        .name(team.getName())
        .build();
  }
}
