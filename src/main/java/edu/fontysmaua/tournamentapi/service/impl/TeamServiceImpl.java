package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.Team.Team;
import edu.fontysmaua.tournamentapi.domain.Team.response.GetAllTeamsResponse;
import edu.fontysmaua.tournamentapi.persistence.TeamRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.TeamEntity;
import edu.fontysmaua.tournamentapi.service.TeamService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final edu.fontysmaua.tournamentapi.mapper.TeamMapper teamMapper;

    public GetAllTeamsResponse getAll() {
        GetAllTeamsResponse res = new GetAllTeamsResponse();
        List<Team> teams = teamMapper.entitiesToModels(teamRepository.findAll());
        res.setTeams(teams);
        return res;
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
