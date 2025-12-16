package edu.fontysmaua.tournamentapi.domain;

import edu.fontysmaua.tournamentapi.enums.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Tournament {

    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String address;
    @NotBlank
    private LocalDateTime startTime;
    @NotBlank
    private LocalDateTime endTime;
    private Status status;
    private String invite;

    @Builder.Default
    private Map<Long, Long> teamCoaches = new HashMap<>();
}
