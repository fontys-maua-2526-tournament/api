package edu.fontysmaua.tournamentapi.controller;

import edu.fontysmaua.tournamentapi.domain.request.SaveMatchRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllMatchesResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetAllUpcomingMatchesResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetMatchByIdResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedMatchResponse;
import edu.fontysmaua.tournamentapi.service.MatchService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

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

    @GetMapping("/{id}")
    public ResponseEntity<GetMatchByIdResponse> getById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(matchService.findById(id));
    }

    @PostMapping
    public ResponseEntity<SavedMatchResponse> create(@RequestBody @Valid SaveMatchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matchService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavedMatchResponse> update(
            @PathVariable @Positive Long id,
            @RequestBody @Valid SaveMatchRequest request
    ) {
        if (!request.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(matchService.update(request));
    }
}
