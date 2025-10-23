package edu.fontysmaua.tournamentapi.domain.Team.response;

import java.util.List;

import edu.fontysmaua.tournamentapi.domain.Team.Team;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetAllTeamsResponse {
  private List<Team> teams;
}
