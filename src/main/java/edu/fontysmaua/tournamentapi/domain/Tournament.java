package edu.fontysmaua.tournamentapi.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Tournament {
    private Long id;
    private String name;
    private String address;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
