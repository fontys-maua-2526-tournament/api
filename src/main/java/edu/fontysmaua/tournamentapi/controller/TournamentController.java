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
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.*;
import edu.fontysmaua.tournamentapi.business.DeleteTournamentUseCase;
import edu.fontysmaua.tournamentapi.business.GetTournamentByIdUseCase;

@RestController
@RequestMapping("/tournaments")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173"})
public class TournamentController {
    private final CreateTournamentUseCase createTournamentUseCase;
    private final TournamentUpdateUseCase tournamentUpdate;
    private final DeleteTournamentUseCase  deleteTournamentService;
    private final GetTournamentByIdUseCase getTournamentByIdUseCase;
  
    @PostMapping
    public ResponseEntity<CreateTournamentResponse> createTournament(@RequestBody @Valid CreateTournamentRequest request) {
        CreateTournamentResponse response = createTournamentUseCase.createTournament(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }
    
    @PutMapping("{id}")
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

    @GetMapping("/{id}")
    public Tournament getTournament(@PathVariable Long id) {
        return getTournamentByIdUseCase.getTournamentById(id);
    }
}
