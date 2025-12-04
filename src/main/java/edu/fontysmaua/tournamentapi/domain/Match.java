package edu.fontysmaua.tournamentapi.domain;

import edu.fontysmaua.tournamentapi.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Match {
    private Long id;
    private Integer round;

    private Long tournamentId;
    private Long team1Id;
    private Long team2Id;

    private Integer team1Score;
    private Integer team2Score;

    private Status status;
}
