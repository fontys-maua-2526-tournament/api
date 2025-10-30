package edu.fontysmaua.tournamentapi.domain.Team.response;

import edu.fontysmaua.tournamentapi.domain.Team.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllTeamsResponse {
    private List<Team> teams = new ArrayList<>();
}
