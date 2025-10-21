package edu.fontysmaua.tournamentapi.controller;

import edu.fontysmaua.tournamentapi.business.DeleteTournamentUseCase;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tournaments")
@AllArgsConstructor
public class TournamentController {
    private final DeleteTournamentUseCase  deleteTournamentService;

    @DeleteMapping("/{id}")
    public Long delete(@PathVariable Long id){
        return deleteTournamentService.delete(id);
    }
}
