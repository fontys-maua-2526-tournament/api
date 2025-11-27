package edu.fontysmaua.tournamentapi.domain;

import edu.fontysmaua.tournamentapi.domain.Match;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedMatchResponse {
    private Match match;
}
