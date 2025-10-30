package edu.fontysmaua.tournamentapi.domain.response;

import edu.fontysmaua.tournamentapi.domain.Tournament;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllTournamentsResponse {
    private List<Tournament> tournaments;
}
