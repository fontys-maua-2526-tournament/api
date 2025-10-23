package edu.fontysmaua.tournamentapi.domain.Team.response;

import java.util.ArrayList;
import java.util.List;

import edu.fontysmaua.tournamentapi.domain.Team.Team;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GetAllTeamsResponse {
  private List<Team> teams = new ArrayList<>();
}
