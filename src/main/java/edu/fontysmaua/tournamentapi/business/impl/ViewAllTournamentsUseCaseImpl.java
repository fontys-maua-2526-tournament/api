package edu.fontysmaua.tournamentapi.business.impl;

import edu.fontysmaua.tournamentapi.domain.Tournament;
import edu.fontysmaua.tournamentapi.persistence.TournamentRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.TournamentEntity;
import edu.fontysmaua.tournamentapi.business.ViewAllTournamentsUseCase;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ViewAllTournamentsUseCaseImpl implements ViewAllTournamentsUseCase {

    private final TournamentRepository tournamentRepo;

    @Transactional(readOnly = true)
    @Override
    public List<Tournament> viewAllTournaments() {
        try {
                List<TournamentEntity> entities = tournamentRepo.findAll(Sort.by(Sort.Direction.ASC, "id"));
                return entities.stream()
                                .map(entity -> new Tournament(
                                                entity.getId(),
                                                entity.getName(),
                                                entity.getAddress(),
                                                entity.getStartTime(),
                                                entity.getEndTime()))
                                .collect(Collectors.toList());
        } catch (Exception e) {
                throw new RuntimeException("Failed to load tournaments", e);
        }
 }
}