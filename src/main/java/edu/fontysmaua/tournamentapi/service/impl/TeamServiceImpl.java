package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.Team.Team;
import edu.fontysmaua.tournamentapi.domain.Team.request.SaveTeamRequest;
import edu.fontysmaua.tournamentapi.domain.Team.response.GetAllTeamsResponse;
import edu.fontysmaua.tournamentapi.domain.Team.response.SavedTeamResponse;
import edu.fontysmaua.tournamentapi.mapper.TeamMapper;
import edu.fontysmaua.tournamentapi.persistence.TeamRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.TeamEntity;
import edu.fontysmaua.tournamentapi.service.TeamService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TeamServiceImpl implements TeamService {
    private TeamRepository teamRepository;
    private TeamMapper teamMapper;

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

        var team = new TeamEntity(request.getId(), request.getName());

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
}
