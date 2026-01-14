package edu.fontysmaua.tournamentapi.controller;

import edu.fontysmaua.tournamentapi.domain.request.AddTeamToTournament;
import edu.fontysmaua.tournamentapi.domain.request.RemoveTeamFromTournamentRequest;
import edu.fontysmaua.tournamentapi.domain.request.SaveTournamentRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllTournamentsResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetMatchesByTournamentRoundResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetTournamentByIdResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetTournamentsByUserIdResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedTournamentResponse;
import edu.fontysmaua.tournamentapi.service.TournamentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/tournaments")
@AllArgsConstructor
@CrossOrigin(origins = { "http://localhost:5173" })
public class TournamentController {
    private final TournamentService tournamentService;

    @GetMapping
    public ResponseEntity<GetAllTournamentsResponse> findAll() {
        return ResponseEntity.ok(tournamentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetTournamentByIdResponse> getById(@PathVariable("id") @Positive Long id) {
        return ResponseEntity.ok(tournamentService.findById(id));
    }

    @PostMapping
    public ResponseEntity<SavedTournamentResponse> create(@RequestBody @Valid SaveTournamentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tournamentService.create(request));

    }

    @PutMapping("{id}")
    public ResponseEntity<SavedTournamentResponse> update(@PathVariable("id") @Positive Long id,
            @RequestBody @Valid SaveTournamentRequest request) {
        if (!request.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(tournamentService.update(request));
    }

    @PostMapping("{tournamentId}/register/{teamId}")
    public ResponseEntity<String> registerTeam(
            @PathVariable @NotNull @Positive Long teamId,
            @PathVariable @NotNull @NotBlank Long tournamentId) {
        if (tournamentService.addTeam(new AddTeamToTournament(teamId, tournamentId))) {
            return ResponseEntity.ok("Team registered successfully");
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("{tournamentId}/withdraw/{teamId}")
    public ResponseEntity<String> withdrawTeam(
            @PathVariable @NotNull @Positive Long teamId,
            @PathVariable @NotNull @NotBlank Long tournamentId) {
        if (tournamentService.removeTeam(new RemoveTeamFromTournamentRequest(teamId, tournamentId))) {
            return ResponseEntity.ok("Team withdrawn successfully");
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> delete(@PathVariable("id") @NotNull @Positive Long id) {
        return ResponseEntity.ok(tournamentService.cancel(id));
    }

    @GetMapping("/{tournamentId}/matches")
    public ResponseEntity<GetMatchesByTournamentRoundResponse> getTournamentMatches(
            @PathVariable @NotNull @Positive Long tournamentId) {
        GetMatchesByTournamentRoundResponse response = 
            tournamentService.getTournamentMatchesByRound(tournamentId, null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{tournamentId}/matches/round/{round}")
    public ResponseEntity<GetMatchesByTournamentRoundResponse> getTournamentMatchesByRound(
            @PathVariable @NotNull @Positive Long tournamentId,
            @PathVariable @NotNull @Min(0) Integer round) {
        GetMatchesByTournamentRoundResponse response = 
            tournamentService.getTournamentMatchesByRound(tournamentId, round);
        return ResponseEntity.ok(response);
    }
}
