package edu.fontysmaua.tournamentapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.fontysmaua.tournamentapi.business.TeamUseCases;
import edu.fontysmaua.tournamentapi.domain.Team.GetAllTeamsResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamsController {
  private final TeamUseCases.getAllTeams getAllTeamsUseCase;

  @GetMapping
  public ResponseEntity<GetAllTeamsResponse> getAllTeams() {
    return ResponseEntity.ok(getAllTeamsUseCase.getAllTeams());
  }


  @DeleteMapping("/{teamId}")
  public ResponseEntity<String> deleteTeam(@PathVariable String teamId) {
      boolean deleted = deleteTeamUseCase.deleteTeamById(teamId);
      if (deleted) {
          return ResponseEntity.ok("Time com ID " + teamId + " foi deletado com sucesso.");
      } else {
          return ResponseEntity.notFound().build(); 
      }
  }
}
