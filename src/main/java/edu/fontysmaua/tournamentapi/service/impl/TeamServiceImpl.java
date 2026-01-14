package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.Team;
import edu.fontysmaua.tournamentapi.domain.request.SaveTeamRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllTeamsResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetTeamsByUserIdResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedTeamResponse;
import edu.fontysmaua.tournamentapi.mapper.TeamMapper;
import edu.fontysmaua.tournamentapi.mapper.UserMapper;
import edu.fontysmaua.tournamentapi.persistence.TeamRepository;
import edu.fontysmaua.tournamentapi.persistence.UserRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.TeamEntity;
import edu.fontysmaua.tournamentapi.persistence.entity.UserEntity;
import edu.fontysmaua.tournamentapi.service.TeamService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TeamServiceImpl implements TeamService {
    private TeamRepository teamRepository;
    private UserRepository userRepository;
    private TeamMapper teamMapper;
    private UserMapper userMapper;

    public GetAllTeamsResponse findAll() {
        GetAllTeamsResponse res = new GetAllTeamsResponse();
        List<Team> teams = teamMapper.entitiesToModels(teamRepository.findAll());
        res.setTeams(teams);
        return res;
    }

    @Override
    public SavedTeamResponse create(SaveTeamRequest request) {
        var team = new TeamEntity();
        team.setName(request.getName());
        team.setInviteCode(request.getInviteCode());

        var saved = teamRepository.save(team);

        return new SavedTeamResponse(teamMapper.entityToModel(saved));
    }

    @Override
    public SavedTeamResponse update(SaveTeamRequest request) {
        if (request.getId() == null || request.getId().equals(0L)) {
            throw new IllegalArgumentException("Id is required");
        }
        if (!teamRepository.existsById(request.getId())) {
            throw new IllegalArgumentException("Team does not exist");
        }

        var team = new TeamEntity();
        team.setId(request.getId());
        team.setName(request.getName());
        team.setInviteCode(request.getInviteCode());

        var updated = teamRepository.save(team);

        return new SavedTeamResponse(teamMapper.entityToModel(updated));
    }

    public Long delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be greater than 0.");
        }
        teamRepository.deleteById(id);
        return id;
    }

    public GetTeamsByUserIdResponse AddUserToTeam(Long userId, String inviteCode) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        TeamEntity teamEntity = teamRepository.findByInviteCode(inviteCode).orElseThrow(() -> new EntityNotFoundException("Team not found with invite code: " + inviteCode));

        userEntity.getTeams().add(teamEntity);
        userRepository.save(userEntity);

        List<TeamEntity> userTeams = new ArrayList<>(userEntity.getTeams());
        GetTeamsByUserIdResponse dto = new GetTeamsByUserIdResponse();

        dto.setTeams(teamMapper.entitiesToModels(userTeams));
        dto.setUser(userMapper.entityToModel(userEntity));
        return dto;
    }
}
