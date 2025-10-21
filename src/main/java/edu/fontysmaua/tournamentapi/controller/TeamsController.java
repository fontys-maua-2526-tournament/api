package edu.fontysmaua.tournamentapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.fontysmaua.tournamentapi.business.TeamUseCases;
import edu.fontysmaua.tournamentapi.domain.Team.GetAllTeamsResponse;
import edu.fontysmaua.tournamentapi.domain.Team.UpdateTeamResponse;
import edu.fontysmaua.tournamentapi.persistence.entity.TeamEntity;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamsController {
  private final TeamUseCases.getAllTeams getAllTeamsUseCase;
  private final TeamUseCases.updateTeam updateTeamUseCase;

  @Autowired
  private TeamUseCases teamUseCases;

  @GetMapping
  public ResponseEntity<GetAllTeamsResponse> getAllTeams() {
    return ResponseEntity.ok(getAllTeamsUseCase.getAllTeams());
  }

<<<<<<< HEAD

  @DeleteMapping("/{teamId}")
  public ResponseEntity<String> deleteTeam(@PathVariable String teamId) {
      boolean deleted = deleteTeamUseCase.deleteTeamById(teamId);
      if (deleted) {
          return ResponseEntity.ok("Time com ID " + teamId + " foi deletado com sucesso.");
      } else {
          return ResponseEntity.notFound().build(); 
      }
=======
  @PutMapping("/{id}")
  public ResponseEntity<UpdateTeamResponse> updateTeam(@PathVariable Long id, @RequestBody TeamEntity team) {
    team.setId(id);
    UpdateTeamResponse response = updateTeamUseCase.updateTeam(team);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/create")
  public void createTeam (@RequestBody TeamEntity team){
    teamUseCases.createTeam(team);
>>>>>>> abcc94d57b69b1864b315361db24381adb1d4e50
  }
}
