package edu.fontysmaua.tournamentapi.controller;

import edu.fontysmaua.tournamentapi.domain.request.SaveTournamentRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllTournamentsResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetTournamentByIdResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetTournamentsByUserIdResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedTournamentResponse;
import edu.fontysmaua.tournamentapi.service.TournamentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tournaments")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173"})
public class TournamentController {
    private final TournamentService tournamentService;

    @GetMapping
    public ResponseEntity<GetAllTournamentsResponse> findAll() {
        return ResponseEntity.ok(tournamentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetTournamentByIdResponse> getById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(tournamentService.findById(id));
    }

    @PostMapping
    public ResponseEntity<SavedTournamentResponse> create(@RequestBody @Valid SaveTournamentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tournamentService.create(request));

    }

    @PutMapping("{id}")
    public ResponseEntity<SavedTournamentResponse> update(@PathVariable @Positive Long id, @RequestBody @Valid SaveTournamentRequest request) {
        if (!request.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(tournamentService.update(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> delete(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(tournamentService.delete(id));
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<Long> cancel(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(tournamentService.cancel(id));
    }
}
