package edu.fontysmaua.tournamentapi.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaveMatchRequest {
    private Long id;
    private Integer round;

    private Long tournamentId;
    private Long team1Id;
    private Long team2Id;

    private Long match1Id;
    private Long match2Id;

    private Integer team1Score;
    private Integer team2Score;
}
