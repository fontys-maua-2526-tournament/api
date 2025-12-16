package edu.fontysmaua.tournamentapi.domain.response;

import edu.fontysmaua.tournamentapi.domain.Match;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetMatchesByTournamentRoundResponse {
    private Long tournamentId;
    private Integer round;
    private List<Match> matches;
}