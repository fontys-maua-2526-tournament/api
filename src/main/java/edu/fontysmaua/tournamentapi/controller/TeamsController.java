package edu.fontysmaua.tournamentapi.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.fontysmaua.tournamentapi.business.TeamUseCases;
import edu.fontysmaua.tournamentapi.domain.Team.response.GetAllTeamsResponse;
import edu.fontysmaua.tournamentapi.persistence.entity.TeamEntity;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamsController {
  private final TeamUseCases.getAllTeams getAllTeamsUseCase;
  private final TeamUseCases.updateTeam updateTeamUseCase;
  private final TeamUseCases.DeleteTeam deleteTeamUseCase;
  private final TeamUseCases.CreateTeam createTeamUseCase;

  @Autowired
  private TeamUseCases teamUseCases;

  @GetMapping
  public ResponseEntity<GetAllTeamsResponse> getAllTeams() {
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
  public ResponseEntity<CreateTeamResponse> createTeam(@RequestBody TeamEntity team) {
        CreateTeamResponse response = createTeamUseCase.createTeam(team);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
