package edu.fontysmaua.tournamentapi.domain.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddTeamToTournament {
    @NotNull
    @Min(1)
    private Long teamId;
    @NotNull
    @Min(1)
    private Long tournamentId;
}
