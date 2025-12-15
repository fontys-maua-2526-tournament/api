package edu.fontysmaua.tournamentapi.domain.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoveTeamFromTournamentRequest {
    @NotBlank
    @Min(1)
    private Long teamId;
    @NotBlank
    @Min(1)
    private Long tournamentId;
}
