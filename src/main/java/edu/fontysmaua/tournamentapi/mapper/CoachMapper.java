package edu.fontysmaua.tournamentapi.mapper;

import edu.fontysmaua.tournamentapi.domain.Coach;
import edu.fontysmaua.tournamentapi.persistence.entity.CoachEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CoachMapper {
    Coach entityToModel(CoachEntity entity);
    CoachEntity modelToEntity(Coach coach);

    List<Coach> entitiesToModels(List<CoachEntity> entities);
    List<CoachEntity> modelsToEntities(List<Coach> coaches);
}
