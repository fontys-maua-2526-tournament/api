`package edu.fontysmaua.tournamentapi.controller;


import edu.fontysmaua.tournamentapi.domain.Team.response.SavedTeamResponse;
import edu.fontysmaua.tournamentapi.persistence.entity.TeamEntity;
import edu.fontysmaua.tournamentapi.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamsController {
    private final TeamService teamService;

    @GetMapping
    public ResponseEntity<> findAll() {
        return ResponseEntity.ok(getAllTeamsUseCase.getAllTeams());
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<DeleteTeamResponse> deleteTeam(@PathVariable String teamId) {
        DeleteTeamResponse response = deleteTeamUseCase.deleteTeamById(teamId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateTeamResponse> updateTeam(@PathVariable Long id, @RequestBody TeamEntity team) {
        team.setId(id);
        UpdateTeamResponse response = updateTeamUseCase.updateTeam(team);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<SavedTeamResponse> createTeam(@RequestBody TeamEntity team) {
        SavedTeamResponse response = createTeamUseCase.createTeam(team);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
`