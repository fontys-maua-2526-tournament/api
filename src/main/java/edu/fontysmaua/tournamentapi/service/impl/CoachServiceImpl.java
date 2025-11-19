package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.request.SaveCoachRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllCoachesResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetTournamentsByUserIdResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedCoachResponse;
import edu.fontysmaua.tournamentapi.exception.NameAlreadyExistsException;
import edu.fontysmaua.tournamentapi.mapper.TournamentMapper;
import edu.fontysmaua.tournamentapi.mapper.UserMapper;
import edu.fontysmaua.tournamentapi.persistence.UserRepository;
import edu.fontysmaua.tournamentapi.persistence.TeamRepository;
import edu.fontysmaua.tournamentapi.persistence.TournamentRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.UserEntity;
import edu.fontysmaua.tournamentapi.service.CoachService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CoachServiceImpl implements CoachService {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TournamentRepository tournamentRepository;
    private final UserMapper userMapper;
    private final TournamentMapper tournamentMapper;

    @Override
    public GetAllCoachesResponse findAll() {
        var coaches = userMapper.entitiesToModels(userRepository.findAll());
        return new GetAllCoachesResponse(coaches);
    }

    @Override
    public GetTournamentsByUserIdResponse findTournamentsByUserId(Long userId) {
        return new GetTournamentsByUserIdResponse(tournamentMapper.entitiesToModels(tournamentRepository.findAllByTeamsUsersId(userId)));
    }

        @Override
    public SavedCoachResponse create(SaveCoachRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new NameAlreadyExistsException("Email already exists");
        }

        var userEntity = new UserEntity();
        userEntity.setFirstName(request.getName());
        userEntity.setEmail(request.getEmail());

        var saved = userRepository.save(userEntity);
        return new SavedCoachResponse(userMapper.entityToModel(saved));
    }

    @Override
    public SavedCoachResponse update(SaveCoachRequest request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("Id is required");
        }

        var existing = userRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Coach not found"));

        existing.setFirstName(request.getName());
        existing.setEmail(request.getEmail());

        var updated = userRepository.save(existing);
        return new SavedCoachResponse(userMapper.entityToModel(updated));
    }

    @Override
    public Long delete(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid coach id");
        }
        userRepository.deleteById(id);
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
