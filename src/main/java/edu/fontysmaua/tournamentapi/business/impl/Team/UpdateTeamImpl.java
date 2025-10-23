package edu.fontysmaua.tournamentapi.business.impl.Team;

import edu.fontysmaua.tournamentapi.domain.Team.request.SaveTeamRequest;
import edu.fontysmaua.tournamentapi.domain.Team.response.UpdateTeamResponse;
import lombok.AllArgsConstructor;

import edu.fontysmaua.tournamentapi.persistence.TeamRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UpdateTeamImpl {
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
  }
}
