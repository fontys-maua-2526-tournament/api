package edu.fontysmaua.tournamentapi.controller;


import edu.fontysmaua.tournamentapi.domain.request.SaveTeamRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllTeamsResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedTeamResponse;
import edu.fontysmaua.tournamentapi.service.TeamService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamsController {
    private final TeamService teamService;

    @GetMapping
    public ResponseEntity<GetAllTeamsResponse> findAll() {
        return ResponseEntity.ok(teamService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteTeam(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(teamService.delete(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavedTeamResponse> updateTeam(@PathVariable Long id, @RequestBody SaveTeamRequest request) {
        if(!request.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(teamService.update(request));
    }

    @PostMapping("/create")
    public ResponseEntity<SavedTeamResponse> createTeam(@RequestBody SaveTeamRequest request) {
        return ResponseEntity.ok(teamService.create(request));
    }
}