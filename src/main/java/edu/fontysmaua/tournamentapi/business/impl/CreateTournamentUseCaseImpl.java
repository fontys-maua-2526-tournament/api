package edu.fontysmaua.tournamentapi.business.impl;

import edu.fontysmaua.tournamentapi.business.CreateTournamentUseCase;
import edu.fontysmaua.tournamentapi.business.exception.NameAlreadyExistsException;
import edu.fontysmaua.tournamentapi.domain.dto.tournament.CreateTournamentRequest;
import edu.fontysmaua.tournamentapi.domain.dto.tournament.CreateTournamentResponse;
import edu.fontysmaua.tournamentapi.persistence.TournamentRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.TournamentEntity;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CreateTournamentUseCaseImpl implements CreateTournamentUseCase {
    private final TournamentRepository tournamentRepository;

    @Transactional
    @Override
    public CreateTournamentResponse createTournament(CreateTournamentRequest request) {
        if (tournamentRepository.existsByName(request.getName())) {
            throw new NameAlreadyExistsException();
        }

        TournamentEntity savedTournament = saveNewTournament(request);

        return CreateTournamentResponse.builder()
                .id(savedTournament.getId())
                .build();
    }

    private TournamentEntity saveNewTournament(CreateTournamentRequest request) {
        TournamentEntity newTournament = TournamentEntity.builder()
                .name(request.getName())
                .address(request.getAddress())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        return tournamentRepository.save(newTournament);
    }
}
