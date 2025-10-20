package edu.fontysmaua.tournamentapi.controller;

import edu.fontysmaua.tournamentapi.business.CreateTournamentUseCase;
import edu.fontysmaua.tournamentapi.domain.dto.tournament.CreateTournamentRequest;
import edu.fontysmaua.tournamentapi.domain.dto.tournament.CreateTournamentResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tournaments")
@AllArgsConstructor
public class TournamentController {
    private final CreateTournamentUseCase createTournamentUseCase;

    @PostMapping
    public ResponseEntity<CreateTournamentResponse> createTournament(@RequestBody @Valid CreateTournamentRequest request) {
        CreateTournamentResponse response = createTournamentUseCase.createTournament(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
