package edu.fontysmaua.tournamentapi.mapper;

import edu.fontysmaua.tournamentapi.domain.Team.Team;
import edu.fontysmaua.tournamentapi.persistence.entity.TeamEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeamMapper {
    Team entityToModel(TeamEntity teamEntity);
    TeamEntity modelToEntity(Team team);

    List<Team> entitiesToModels(List<TeamEntity> teamEntities);
    List<TeamEntity> modelsToEntities(List<Team> teams);
}
