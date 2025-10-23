package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.Team.request.SaveTeamRequest;
import edu.fontysmaua.tournamentapi.domain.Team.response.SavedTeamResponse;
import edu.fontysmaua.tournamentapi.mapper.TeamMapper;
import edu.fontysmaua.tournamentapi.persistence.TeamRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.TeamEntity;
import edu.fontysmaua.tournamentapi.service.TeamService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TeamServiceImpl implements TeamService {
    private TeamRepository teamRepository;
    private TeamMapper teamMapper;

    @Override
    public SavedTeamResponse createTeam(SaveTeamRequest request) {
        var team = new TeamEntity();
        team.setName(request.getName());

        var saved = teamRepository.save(team);

        return new SavedTeamResponse(teamMapper.entityToModel(saved));
    }

    @Override
    public SavedTeamResponse updateTeam(SaveTeamRequest request) {
        if (request.getId() == null || request.getId().equals(0L)) {
            throw new IllegalArgumentException("Id is required");
        }
        if (!teamRepository.existsById(request.getId())) {
            throw new IllegalArgumentException("Team does not exist");
        }

        var team = new TeamEntity(request.getId(), request.getName());

        var updated = teamRepository.save(team);

        return new SavedTeamResponse(teamMapper.entityToModel(updated));
    }
}
