package edu.fontysmaua.tournamentapi.controller;

import edu.fontysmaua.tournamentapi.business.CreateTournamentUseCase;
import edu.fontysmaua.tournamentapi.domain.dto.tournament.CreateTournamentRequest;
import edu.fontysmaua.tournamentapi.domain.dto.tournament.CreateTournamentResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import edu.fontysmaua.tournamentapi.domain.Tournament;
import edu.fontysmaua.tournamentapi.business.TournamentUpdateUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import edu.fontysmaua.tournamentapi.business.DeleteTournamentUseCase;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tournaments")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173"})
public class TournamentController {
    private final CreateTournamentUseCase createTournamentUseCase;
    private final TournamentUpdateUseCase tournamentUpdate;
    private final DeleteTournamentUseCase  deleteTournamentService;

  
    @PostMapping
    public ResponseEntity<CreateTournamentResponse> createTournament(@RequestBody @Valid CreateTournamentRequest request) {
        CreateTournamentResponse response = createTournamentUseCase.createTournament(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }
    
    @PostMapping("{id}")
    public ResponseEntity<Tournament> updateTournament(@PathVariable @Positive Long id, @RequestBody @Valid Tournament tournament) {
        if (!tournament.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(tournamentUpdate.updateTournament(tournament));
    }

    @DeleteMapping("/{id}")
    public Long delete(@PathVariable @Positive Long id) {
        return deleteTournamentService.delete(id);
    }
}
