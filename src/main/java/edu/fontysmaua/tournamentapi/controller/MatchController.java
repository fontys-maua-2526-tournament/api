package edu.fontysmaua.tournamentapi.controller;

import edu.fontysmaua.tournamentapi.domain.response.GetAllMatchesResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetAllUpcomingMatchesResponse;
import edu.fontysmaua.tournamentapi.service.MatchService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173"})
public class MatchController {
    private final MatchService matchService;

    @GetMapping
    public ResponseEntity<GetAllMatchesResponse> findAll() {
        return ResponseEntity.ok(matchService.findAll());
    }

    @GetMapping("/upcoming")
    public ResponseEntity<GetAllUpcomingMatchesResponse> findAllUpcoming() {
        return ResponseEntity.ok(matchService.findAllUpcoming());
    }

    @GetMapping("/cancel/{id}")
    public ResponseEntity<Long> cancel (@PathVariable @Positive Long id){
        return ResponseEntity.ok(matchService.cancelMatch(id));
    }
}
