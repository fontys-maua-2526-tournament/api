package edu.fontysmaua.tournamentapi.mapper;

import edu.fontysmaua.tournamentapi.domain.Team;
import edu.fontysmaua.tournamentapi.persistence.entity.TeamEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TeamMapper {
    Team entityToModel(TeamEntity teamEntity);
    
    @Mapping(target = "coach", ignore = true)
    @Mapping(target = "members", ignore = true)
    TeamEntity modelToEntity(Team team);

    List<Team> entitiesToModels(List<TeamEntity> teamEntities);

    List<TeamEntity> modelsToEntities(List<Team> teams);
}
