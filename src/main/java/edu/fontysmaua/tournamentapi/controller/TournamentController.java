package edu.fontysmaua.tournamentapi.controller;

import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import edu.fontysmaua.tournamentapi.business.ViewAllTournamentsUseCase;
import edu.fontysmaua.tournamentapi.domain.Tournament;

@RestController
@RequestMapping("/tournaments")
@AllArgsConstructor
@CrossOrigin
public class TournamentController {
    private final ViewAllTournamentsUseCase viewAllTournamentsView;

    @GetMapping
    public ResponseEntity<List<Tournament>> viewAllTournaments() {
        List<Tournament> tournaments = viewAllTournamentsView.viewAllTournaments();
        return ResponseEntity.ok(tournaments);
    }
}
