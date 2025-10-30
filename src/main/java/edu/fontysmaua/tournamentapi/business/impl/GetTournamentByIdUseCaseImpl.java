package edu.fontysmaua.tournamentapi.business.impl;

import edu.fontysmaua.tournamentapi.business.GetTournamentByIdUseCase;
import edu.fontysmaua.tournamentapi.domain.Tournament;
import edu.fontysmaua.tournamentapi.persistence.TournamentRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.TournamentEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service

public class GetTournamentByIdUseCaseImpl implements GetTournamentByIdUseCase {
    private final TournamentRepository tournamentRepository;

    @Override
    public Tournament getTournamentById(Long id){
        if(id == null){
            throw new IllegalArgumentException("ID cannot be null");
        }
        if(id <= 0){
            throw new IllegalArgumentException("ID must be greater than 0");
        }

        TournamentEntity entity = tournamentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));

        return Tournament.builder()
            .id(entity.getId())
            .name(entity.getName())
            .address(entity.getAddress())
            .startTime(entity.getStartTime())
            .endTime(entity.getEndTime())
            .build();
    }
}
