package edu.fontysmaua.tournamentapi.domain.request;

import edu.fontysmaua.tournamentapi.enums.Status;
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
public class SaveTournamentRequest {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String address;
    @NotNull
    private LocalDateTime startTime;
    @NotNull
    private LocalDateTime endTime;
    private Status status;
}
