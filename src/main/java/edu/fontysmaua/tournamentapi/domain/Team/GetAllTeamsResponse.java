package edu.fontysmaua.tournamentapi.domain.Team;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetAllTeamsResponse {
  private List<Team> teams;
}
