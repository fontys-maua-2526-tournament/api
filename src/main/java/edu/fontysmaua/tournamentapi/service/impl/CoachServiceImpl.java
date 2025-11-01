package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.Coach;
import edu.fontysmaua.tournamentapi.domain.request.SaveCoachRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllCoachesResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedCoachResponse;
import edu.fontysmaua.tournamentapi.exception.NameAlreadyExistsException;
import edu.fontysmaua.tournamentapi.mapper.CoachMapper;
import edu.fontysmaua.tournamentapi.persistence.CoachRepository;
import edu.fontysmaua.tournamentapi.persistence.TeamRepository;
import edu.fontysmaua.tournamentapi.persistence.TournamentRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.CoachEntity;
import edu.fontysmaua.tournamentapi.service.CoachService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CoachServiceImpl implements CoachService {
    private final CoachRepository coachRepository;
    private final TeamRepository teamRepository;
    private final TournamentRepository tournamentRepository;
    private final CoachMapper coachMapper;

    @Override
    public GetAllCoachesResponse findAll() {
        var coaches = coachMapper.entitiesToModels(coachRepository.findAll());
        return new GetAllCoachesResponse(coaches);
    }

    @Override
    public SavedCoachResponse create(SaveCoachRequest request) {
        if (coachRepository.existsByEmail(request.getEmail())) {
            throw new NameAlreadyExistsException("Email already exists");
        }

        var coachEntity = new CoachEntity();
        coachEntity.setName(request.getName());
        coachEntity.setEmail(request.getEmail());

        var saved = coachRepository.save(coachEntity);
        return new SavedCoachResponse(coachMapper.entityToModel(saved));
    }

    @Override
    public SavedCoachResponse update(SaveCoachRequest request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("Id is required");
        }

        var existing = coachRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Coach not found"));

        existing.setName(request.getName());
        existing.setEmail(request.getEmail());

        var updated = coachRepository.save(existing);
        return new SavedCoachResponse(coachMapper.entityToModel(updated));
    }

    @Override
    public Long delete(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid coach id");
        }
        coachRepository.deleteById(id);
        return id;
    }

    @Override
    public void disbandTeam(Long teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new IllegalArgumentException("Team does not exist");
        }
        teamRepository.deleteById(teamId);
    }

    @Override
    public void registerTeamInTournament(Long teamId, Long tournamentId) {
        if (!teamRepository.existsById(teamId)) {
            throw new IllegalArgumentException("Team not found");
        }
        if (!tournamentRepository.existsById(tournamentId)) {
            throw new IllegalArgumentException("Tournament not found");
        }
        System.out.printf("Team %d registered in tournament %d%n", teamId, tournamentId);
    }

    @Override
    public void withdrawTeamFromTournament(Long teamId, Long tournamentId) {
        if (!teamRepository.existsById(teamId)) {
            throw new IllegalArgumentException("Team not found");
        }
        if (!tournamentRepository.existsById(tournamentId)) {
            throw new IllegalArgumentException("Tournament not found");
        }
        System.out.printf("Team %d withdrawn from tournament %d%n", teamId, tournamentId);
    }
}
