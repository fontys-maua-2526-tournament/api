package edu.fontysmaua.tournamentapi.domain.Team.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveTeamRequest {
    private Long id;
    @NotBlank
    private String name;
}
