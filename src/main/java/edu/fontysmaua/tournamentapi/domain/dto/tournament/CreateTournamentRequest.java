package edu.fontysmaua.tournamentapi.domain.dto.tournament;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTournamentRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String address;
    @NotNull
    private LocalDateTime startTime;
    @NotNull
    private LocalDateTime endTime;
}
