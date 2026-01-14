package edu.fontysmaua.tournamentapi.controller;

import edu.fontysmaua.tournamentapi.domain.request.JoinTeamRequest;
import edu.fontysmaua.tournamentapi.domain.response.TeamMemberResponse;
import edu.fontysmaua.tournamentapi.service.AthleteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/athletes")
@RequiredArgsConstructor
public class AthleteController {
    private final AthleteService athleteService;

    /**
     * Allows an adult athlete to join a team using an invite code.
     * Underage athletes must be added by their coach directly.
     */
    @PostMapping("/join-team")
    public ResponseEntity<TeamMemberResponse> joinTeamViaInvite(@RequestBody @Valid JoinTeamRequest request) {
        return ResponseEntity.ok(athleteService.joinTeamViaInvite(request));
    }
}

