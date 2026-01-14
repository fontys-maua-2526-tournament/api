package edu.fontysmaua.tournamentapi.domain.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddAthleteToTeamRequest {
    @NotNull
    @Positive
    private Long teamId;

    @NotNull
    @Positive
    private Long athleteId;
}

