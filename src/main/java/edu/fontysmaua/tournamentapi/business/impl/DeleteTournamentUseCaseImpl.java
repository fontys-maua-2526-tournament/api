package edu.fontysmaua.tournamentapi.business.impl;

import edu.fontysmaua.tournamentapi.business.DeleteTournamentUseCase;
import edu.fontysmaua.tournamentapi.persistence.TournamentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class DeleteTournamentUseCaseImpl implements DeleteTournamentUseCase {
    private final TournamentRepository tournamentRepository;

    public Long delete(Long id) {
        tournamentRepository.deleteById(id);
        return id;
    }
}
