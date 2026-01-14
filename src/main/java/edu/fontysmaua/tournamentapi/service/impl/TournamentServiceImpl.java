package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.Match;
import edu.fontysmaua.tournamentapi.domain.request.AddTeamToTournament;
import edu.fontysmaua.tournamentapi.domain.request.RemoveTeamFromTournamentRequest;
import edu.fontysmaua.tournamentapi.domain.request.SaveTournamentRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllTournamentsResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetMatchesByTournamentRoundResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetTournamentByIdResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetTournamentsByUserIdResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedTournamentResponse;
import edu.fontysmaua.tournamentapi.enums.Status;
import edu.fontysmaua.tournamentapi.mapper.MatchMapper;
import edu.fontysmaua.tournamentapi.mapper.TeamMapper;
import edu.fontysmaua.tournamentapi.mapper.TournamentMapper;
import edu.fontysmaua.tournamentapi.persistence.MatchRepository;
import edu.fontysmaua.tournamentapi.persistence.TeamRepository;
import edu.fontysmaua.tournamentapi.persistence.TournamentRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.MatchEntity;
import edu.fontysmaua.tournamentapi.persistence.entity.TeamEntity;
import edu.fontysmaua.tournamentapi.persistence.entity.TournamentEntity;
import edu.fontysmaua.tournamentapi.service.TournamentService;
import jakarta.persistence.EntityExistsException;
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

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;

    @Override
    public GetAllTournamentsResponse findAll() {
        List<TournamentEntity> tournaments = tournamentRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
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
    public GetTournamentsByUserIdResponse getByUserId(Long userId) {
        return new GetTournamentsByUserIdResponse(tournamentMapper.entitiesToModels(tournamentRepository.findAllByTeamsMembersId(userId)));
    }

    @Override
    public SavedTournamentResponse create(SaveTournamentRequest request) {
        if (tournamentRepository.existsByName(request.getName())) {
            throw new EntityExistsException("Tournament with this name already exists");
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
                .status(request.getStatus())
                .build();

        var response = tournamentRepository.save(entity);

        return new SavedTournamentResponse(tournamentMapper.entityToModel(response));
    }

    @Override
    public Boolean addTeam(AddTeamToTournament request) {
        TeamEntity team = teamRepository.findById(request.getTeamId())
            .orElseThrow(() -> new IllegalArgumentException("Team not found with ID: " + request.getTeamId()));

        TournamentEntity tournament = tournamentRepository.findById(request.getTournamentId())
            .orElseThrow(() -> new IllegalArgumentException("Tournament not found with ID: " + request.getTournamentId()));

        if (tournament.getStartTime() != null && tournament.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot register team after tournament has started");
        }

        if (tournament.getTeams() == null) {
            tournament.setTeams(new ArrayList<>());
        }

        boolean isAlreadyRegistered = tournament.getTeams().stream()
            .anyMatch(t -> t.getId().equals(request.getTeamId()));
    
        if (isAlreadyRegistered) {
            throw new IllegalStateException(
                String.format("Team %s is already registered in tournament %s", 
                    team.getName(), tournament.getName())
            );
        }

        tournament.getTeams().add(team);

        if (team.getTournaments() == null) {
            team.setTournaments(new ArrayList<>());
        }
        team.getTournaments().add(tournament);

        tournamentRepository.save(tournament);
        teamRepository.save(team);

        System.out.printf("Team '%s' (ID: %d) successfully registered in tournament '%s' (ID: %d)%n", 
                team.getName(), request.getTeamId(), tournament.getName(), request.getTournamentId());
        
        return true;
    }

    @Override
    public Boolean removeTeam(RemoveTeamFromTournamentRequest request) {
        TeamEntity team = teamRepository.findById(request.getTeamId())
            .orElseThrow(() -> new IllegalArgumentException("Team not found with ID: " + request.getTeamId()));
        TournamentEntity tournament = tournamentRepository.findById(request.getTournamentId())
            .orElseThrow(() -> new IllegalArgumentException("Tournament not found with ID: " + request.getTournamentId()));
        
        if (tournament.getStartTime() != null && tournament.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot remove team after tournament has started");
        }

        boolean isRegistered = tournament.getTeams() != null && tournament.getTeams().stream().anyMatch(t -> t.getId().equals(request.getTeamId()));
    
        if (!isRegistered) {
            throw new IllegalStateException(
                String.format("Team %s is not registered in tournament %s", 
                    team.getName(), tournament.getName())
            );
        }

        tournament.getTeams().removeIf(t -> t.getId().equals(request.getTeamId()));

        if (team.getTournaments() != null) {
            team.getTournaments().removeIf(t -> t.getId().equals(request.getTournamentId()));
        }

        tournamentRepository.save(tournament);
        teamRepository.save(team);

        System.out.printf("Team '%s' (ID: %d) successfully removed from tournament '%s' (ID: %d)%n", 
                team.getName(), request.getTeamId(), tournament.getName(), request.getTournamentId());

        return true;
    }

    public Long cancel(Long tournamentId) {
        TournamentEntity tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new EntityNotFoundException("Tournament not found"));
        tournament.setStatus(Status.CANCELLED);
        tournamentRepository.save(tournament);
        return tournamentId;
    }

    @Override
    public GetMatchesByTournamentRoundResponse getTournamentMatchesByRound(Long tournamentId, Integer round) {
        if (tournamentId == null || tournamentId <= 0) {
            throw new IllegalArgumentException("Tournament ID must be greater than 0");
        }
        
        if (!tournamentRepository.existsById(tournamentId)) {
            throw new IllegalArgumentException("Tournament not found with ID: " + tournamentId);
        }
        
        if (round != null && round < 0) {
            throw new IllegalArgumentException("Round must be a non-negative integer");
        }
        
        List<MatchEntity> matchEntities;
        
        if (round != null) {
            matchEntities = matchRepository.findByTournamentIdAndRound(tournamentId, round);
        } else {
            matchEntities = matchRepository.findByTournamentId(tournamentId);
        }
        
        List<Match> matches = matchMapper.entitiesToModels(matchEntities);
        
        return new GetMatchesByTournamentRoundResponse(tournamentId, round, matches);
    }
}
