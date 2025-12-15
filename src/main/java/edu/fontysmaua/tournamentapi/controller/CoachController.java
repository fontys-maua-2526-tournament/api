package edu.fontysmaua.tournamentapi.controller;

import edu.fontysmaua.tournamentapi.domain.request.AddAthleteToTeamRequest;
import edu.fontysmaua.tournamentapi.domain.request.SaveCoachRequest;
import edu.fontysmaua.tournamentapi.domain.request.UpdateTeamRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllCoachesResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetTournamentsByUserIdResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedCoachResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedTeamResponse;
import edu.fontysmaua.tournamentapi.domain.response.TeamMemberResponse;
import edu.fontysmaua.tournamentapi.service.CoachService;
import jakarta.validation.Valid;
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

    @GetMapping("{id}/tournaments")
    public ResponseEntity<GetTournamentsByUserIdResponse> getTournamentsByUserId(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(coachService.findTournamentsByUserId(id));
    }

    @PostMapping("/create")
    public ResponseEntity<SavedCoachResponse> create(@RequestBody @Valid SaveCoachRequest request) {
        return ResponseEntity.ok(coachService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavedCoachResponse> update(@PathVariable Long id, @RequestBody @Valid SaveCoachRequest request) {
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

    /**
     * Add an underage athlete directly to a team (no invite code needed).
     * Only the coach of the team can add underage athletes.
     */
    @PostMapping("/{coachId}/teams/add-athlete")
    public ResponseEntity<TeamMemberResponse> addUnderageAthleteToTeam(
            @PathVariable @Positive Long coachId,
            @RequestBody @Valid AddAthleteToTeamRequest request) {
        return ResponseEntity.ok(coachService.addUnderageAthleteToTeam(request, coachId));
    }

    /**
     * Update a team's information.
     * Only the coach of the team can update it.
     */
    @PutMapping("/{coachId}/teams/{teamId}")
    public ResponseEntity<SavedTeamResponse> updateTeam(
            @PathVariable @Positive Long coachId,
            @PathVariable @Positive Long teamId,
            @RequestBody @Valid UpdateTeamRequest request) {
        if (!teamId.equals(request.getId())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(coachService.updateTeam(request, coachId));
    }
}

