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
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null.");
        }
        if(id <= 0){
            throw new IllegalArgumentException("ID must be greater than 0.");
        }
        tournamentRepository.deleteById(id);
        return id;
    }
}
