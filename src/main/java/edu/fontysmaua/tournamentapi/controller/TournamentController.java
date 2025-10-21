package edu.fontysmaua.tournamentapi.controller;

import edu.fontysmaua.tournamentapi.domain.Tournament;
import edu.fontysmaua.tournamentapi.business.TournamentUpdateUseCase;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tournaments")
@AllArgsConstructor
public class TournamentController {

    private final TournamentUpdateUseCase tournamentUpdate;

    @PostMapping("{id}")
    public ResponseEntity<Tournament> updateTournament(@PathVariable Long id, @RequestBody @Valid Tournament tournament) {
        if(!tournament.getId().equals(id)) {
           return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(tournamentUpdate.updateTournament(tournament));
    }
}
