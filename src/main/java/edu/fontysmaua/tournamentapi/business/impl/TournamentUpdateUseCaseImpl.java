package edu.fontysmaua.tournamentapi.business.impl;

import edu.fontysmaua.tournamentapi.domain.Tournament;
import edu.fontysmaua.tournamentapi.persistence.TournamentRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.TournamentEntity;
import edu.fontysmaua.tournamentapi.business.TournamentUpdateUseCase;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class TournamentUpdateUseCaseImpl implements TournamentUpdateUseCase {

    private final TournamentRepository tournamentRepo;

    @Override
    @Transactional
    public Tournament updateTournament(Tournament tournament) {
        if (tournament == null) {
            throw new IllegalArgumentException("Tournament cannot be null");
        }

        if(tournament.getId() == null) {
            throw new IllegalArgumentException("Tournament ID cannot be null");
        }

        TournamentEntity entity = TournamentEntity.builder()
                        .id(tournament.getId())
                        .name(tournament.getName())
                        .address(tournament.getAddress())
                        .startTime(tournament.getStartTime())
                        .endTime(tournament.getEndTime())
                        .build();

        var response = tournamentRepo.save(entity);

        return new Tournament(response.getId(), response.getName(), response.getAddress(), response.getStartTime(), response.getEndTime());
    }
}
