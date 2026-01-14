package edu.fontysmaua.tournamentapi.controller;

import edu.fontysmaua.tournamentapi.domain.UserDto;
import edu.fontysmaua.tournamentapi.domain.request.AddAthleteToTeamRequest;
import edu.fontysmaua.tournamentapi.domain.request.RegisterRequest;
import edu.fontysmaua.tournamentapi.domain.request.UpdateTeamRequest;
import edu.fontysmaua.tournamentapi.domain.request.UpdateUserRequest;
import edu.fontysmaua.tournamentapi.domain.response.*;
import edu.fontysmaua.tournamentapi.enums.UserRole;
import edu.fontysmaua.tournamentapi.service.AuthService;
import edu.fontysmaua.tournamentapi.service.CoachService;
import edu.fontysmaua.tournamentapi.service.TournamentService;
import edu.fontysmaua.tournamentapi.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coaches")
@RequiredArgsConstructor
public class CoachController {
    private final UserService userService;
    private final TournamentService tournamentService;
    private final AuthService authService;
    private final CoachService coachService;

    @GetMapping
    public ResponseEntity<GetAllUsersResponse> findAll() {
        return ResponseEntity.ok(userService.findByRole(UserRole.COACH));
    }

    @GetMapping("{id}/tournaments")
    public ResponseEntity<GetTournamentsByUserIdResponse> getTournamentsByUserId(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(tournamentService.getByUserId(id));
    }

    @PostMapping("/create")
    public ResponseEntity<AuthResponse> create(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        if (!id.equals(request.getId())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(userService.update(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable @Positive Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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

