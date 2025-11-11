package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.request.SaveTournamentRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllTournamentsResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetTournamentByIdResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedTournamentResponse;
import edu.fontysmaua.tournamentapi.enums.Status;
import edu.fontysmaua.tournamentapi.exception.NameAlreadyExistsException;
import edu.fontysmaua.tournamentapi.mapper.TournamentMapper;
import edu.fontysmaua.tournamentapi.persistence.TournamentRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.TournamentEntity;
import edu.fontysmaua.tournamentapi.service.TournamentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TournamentServiceImpl implements TournamentService {
    private final TournamentRepository tournamentRepository;
    private final TournamentMapper tournamentMapper;

    @Override
    public GetAllTournamentsResponse findAll() {
        List<TournamentEntity> tournaments = tournamentRepository.findAll();
        GetAllTournamentsResponse response = new GetAllTournamentsResponse();
        response.setTournaments(tournamentMapper.entitiesToModels(tournaments));
        return response;
    }

    @Override
    public GetTournamentByIdResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be greater than 0");
        }

        TournamentEntity entity = tournamentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tournament not found"));

        return new GetTournamentByIdResponse(tournamentMapper.entityToModel(entity));
    }

    @Override
    public SavedTournamentResponse create(SaveTournamentRequest request) {
        if (tournamentRepository.existsByName(request.getName())) {
            throw new NameAlreadyExistsException();
        }

        Status status = null;

        if (request.getStartTime() != null) {
            if (request.getStartTime().isAfter(LocalDateTime.now())) {
                status = Status.SCHEDULED;
            } else {
                status = Status.COMPLETED;
            }
        }

        TournamentEntity savedTournament = tournamentRepository
                .save(TournamentEntity.builder()
                        .name(request.getName())
                        .address(request.getAddress())
                        .startTime(request.getStartTime())
                        .endTime(request.getEndTime())
                        .status(status)
                        .build()
                );

        return new SavedTournamentResponse(tournamentMapper.entityToModel(savedTournament));
    }

    @Override
    public SavedTournamentResponse update(SaveTournamentRequest request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("Tournament ID cannot be null or zero");
        }

        if (!tournamentRepository.existsById(request.getId())) {
            throw new EntityNotFoundException("Tournament doesn't exist in the database");
        }

        TournamentEntity entity = TournamentEntity.builder()
                .id(request.getId())
                .name(request.getName())
                .address(request.getAddress())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        var response = tournamentRepository.save(entity);

        return new SavedTournamentResponse(tournamentMapper.entityToModel(response));
    }

    @Override
    public Long delete(Long tournamentId) {
        if (tournamentId == null) {
            throw new IllegalArgumentException("ID cannot be null.");
        }
        if (tournamentId <= 0) {
            throw new IllegalArgumentException("ID must be greater than 0.");
        }
        if (!tournamentRepository.existsById(tournamentId)) {
            throw new EntityNotFoundException("Tournament doesn't exist in the database");
        }

        tournamentRepository.deleteById(tournamentId);
        return tournamentId;
    }

    public Long cancel(Long tournamentId) {
        TournamentEntity tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new EntityNotFoundException("Tournament not found"));
        tournament.setStatus(Status.CANCELLED);
        tournamentRepository.save(tournament);
        return tournamentId;
    }
}
