package edu.fontysmaua.tournamentapi.domain.request;

import edu.fontysmaua.tournamentapi.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    private Integer team1Score;
    private Integer team2Score;
}
