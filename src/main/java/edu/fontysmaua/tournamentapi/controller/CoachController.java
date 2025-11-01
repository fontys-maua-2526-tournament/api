package edu.fontysmaua.tournamentapi.controller;

import edu.fontysmaua.tournamentapi.domain.request.SaveCoachRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllCoachesResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedCoachResponse;
import edu.fontysmaua.tournamentapi.service.CoachService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coaches")
@RequiredArgsConstructor
public class CoachController {
    private final CoachService coachService;

    @GetMapping
    public ResponseEntity<GetAllCoachesResponse> findAll() {
        return ResponseEntity.ok(coachService.findAll());
    }

    @PostMapping("/create")
    public ResponseEntity<SavedCoachResponse> create(@RequestBody SaveCoachRequest request) {
        return ResponseEntity.ok(coachService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavedCoachResponse> update(@PathVariable Long id, @RequestBody SaveCoachRequest request) {
        if (!id.equals(request.getId())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(coachService.update(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> delete(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(coachService.delete(id));
    }

    @DeleteMapping("/disband/{teamId}")
    public ResponseEntity<String> disbandTeam(@PathVariable Long teamId) {
        coachService.disbandTeam(teamId);
        return ResponseEntity.ok("Team disbanded successfully");
    }

    @PostMapping("/register/{teamId}/tournament/{tournamentId}")
    public ResponseEntity<String> registerTeam(@PathVariable Long teamId, @PathVariable Long tournamentId) {
        coachService.registerTeamInTournament(teamId, tournamentId);
        return ResponseEntity.ok("Team registered successfully");
    }

    @PostMapping("/withdraw/{teamId}/tournament/{tournamentId}")
    public ResponseEntity<String> withdrawTeam(@PathVariable Long teamId, @PathVariable Long tournamentId) {
        coachService.withdrawTeamFromTournament(teamId, tournamentId);
        return ResponseEntity.ok("Team withdrawn successfully");
    }
}
