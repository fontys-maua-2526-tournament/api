package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.Team.UpdateTeamResponse;
import edu.fontysmaua.tournamentapi.domain.Team.request.SaveTeamRequest;
import edu.fontysmaua.tournamentapi.persistence.TeamRepository;

public class UpdateTeamImpl {
  @Autowired
  private TeamRepository teamRepository;

  public UpdateTeamResponse updateTeam(SaveTeamRequest team) {
    if (team == null) {
      throw new IllegalArgumentException("Team must not be null");
    }
    if (team.getId() == null) {
      throw new IllegalArgumentException("Team id must not be null");
    }
    var existing = teamRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Team not found with id: " + id));
    org.springframework.beans.BeanUtils.copyProperties(team, existing, "id");
    teamRepository.save(existing);
    return new UpdateTeamResponse(
            team.getId().toString()
    );
  }
}
