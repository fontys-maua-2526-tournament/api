package edu.fontysmaua.tournamentapi.domain.Team.response;


import edu.fontysmaua.tournamentapi.domain.Team.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavedTeamResponse {
    private Team team;
}
