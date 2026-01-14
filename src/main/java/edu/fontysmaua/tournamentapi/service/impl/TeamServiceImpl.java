package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.Team;
import edu.fontysmaua.tournamentapi.domain.request.AddAthleteToTeamRequest;
import edu.fontysmaua.tournamentapi.domain.request.SaveTeamRequest;
import edu.fontysmaua.tournamentapi.domain.response.*;
import edu.fontysmaua.tournamentapi.enums.UserRole;
import edu.fontysmaua.tournamentapi.mapper.TeamMapper;
import edu.fontysmaua.tournamentapi.mapper.UserMapper;
import edu.fontysmaua.tournamentapi.persistence.TeamRepository;
import edu.fontysmaua.tournamentapi.persistence.UserRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.TeamEntity;
import edu.fontysmaua.tournamentapi.persistence.entity.UserEntity;
import edu.fontysmaua.tournamentapi.service.TeamService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public TeamMemberResponse addAthlete(AddAthleteToTeamRequest req, String coachEmail) {
        userRepository.findByEmailAndUserRole(coachEmail, UserRole.COACH)
                .orElseThrow(() -> new EntityNotFoundException("This coach doesn't exist"));
        var athlete = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("This athlete doesn't exist"));

        TeamEntity team = teamRepository.getReferenceById(req.getTeamId());

        if(!team.getCoach().getEmail().equals(coachEmail))
            throw new IllegalArgumentException("You are not the coach of this team");

        if(team.getMembers().contains(athlete))
            throw new EntityExistsException("Athlete already a member of this team");

        team.getMembers().add(athlete);

        return new TeamMemberResponse(
                teamMapper.entityToModel(teamRepository.save(team)),
                "Added athlete to the team"
        );
    }

    @Transactional
    public GetTeamsByUserIdResponse joinTeamViaInvite (Long userId, String inviteCode) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        TeamEntity teamEntity = teamRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with invite code: " + inviteCode));

        // Synchronize both sides of the bidirectional relationship
        userEntity.getTeams().add(teamEntity);
        teamEntity.getMembers().add(userEntity);

        userRepository.save(userEntity);
        teamRepository.save(teamEntity);

        List<TeamEntity> userTeams = new ArrayList<>(userEntity.getTeams());
        GetTeamsByUserIdResponse dto = new GetTeamsByUserIdResponse();

        dto.setTeams(teamMapper.entitiesToModels(userTeams));
        dto.setUser(userMapper.entityToModel(userEntity));
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public GetTeamMembersResponse getTeamMembers(Long teamId) {
        TeamEntity teamEntity = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + teamId));

        return new GetTeamMembersResponse(userMapper.entitiesToModels(teamEntity.getMembers()));
    }
}
