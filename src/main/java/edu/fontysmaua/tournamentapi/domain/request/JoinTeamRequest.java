package edu.fontysmaua.tournamentapi.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JoinTeamRequest {
    @NotBlank
    private String inviteCode;

    @NotNull
    @Positive
    private Long athleteId;
}

