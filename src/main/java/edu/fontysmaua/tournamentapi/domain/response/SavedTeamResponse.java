package edu.fontysmaua.tournamentapi.domain.response;


import edu.fontysmaua.tournamentapi.domain.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavedTeamResponse {
    private Team team;
}
