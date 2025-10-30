package edu.fontysmaua.tournamentapi.domain.response;

import edu.fontysmaua.tournamentapi.domain.Tournament;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavedTournamentResponse {
    private Tournament tournament;
}
