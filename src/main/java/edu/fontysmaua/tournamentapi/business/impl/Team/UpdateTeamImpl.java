package edu.fontysmaua.tournamentapi.business.impl.Team;

import org.springframework.beans.factory.annotation.Autowired;

import edu.fontysmaua.tournamentapi.persistence.TeamRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.TeamEntity;

public class UpdateTeamImpl {
  @Autowired
  private TeamRepository teamRepository;

  public void updateTeam(TeamEntity team) {
    if (team == null) {
      throw new IllegalArgumentException("team must not be null");
    }
    var id = team.getId();
    if (id == null) {
      throw new IllegalArgumentException("team id must not be null");
    }
    var existing = teamRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Team not found with id: " + id));
    org.springframework.beans.BeanUtils.copyProperties(team, existing, "id");
    teamRepository.save(existing);
  }
}
