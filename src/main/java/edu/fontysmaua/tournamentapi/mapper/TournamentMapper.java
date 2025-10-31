package edu.fontysmaua.tournamentapi.mapper;

import edu.fontysmaua.tournamentapi.domain.Team;
import edu.fontysmaua.tournamentapi.domain.Tournament;
import edu.fontysmaua.tournamentapi.persistence.entity.TeamEntity;
import edu.fontysmaua.tournamentapi.persistence.entity.TournamentEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TournamentMapper {
    Tournament entityToModel(TournamentEntity tournamentEntity);
    TournamentEntity modelToEntity(Tournament tournament);

    List<Tournament> entitiesToModels(List<TournamentEntity> tournamentEntities);
    List<TournamentEntity> modelsToEntities(List<Tournament> tournaments);
}
