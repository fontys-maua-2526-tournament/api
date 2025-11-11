package edu.fontysmaua.tournamentapi.mapper;

import edu.fontysmaua.tournamentapi.domain.Match;
import edu.fontysmaua.tournamentapi.persistence.entity.MatchEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MatchMapper {
    @Mappings({
            @Mapping(source = "tournament.id", target = "tournamentId"),
            @Mapping(source = "team1.id", target = "team1Id"),
            @Mapping(source = "team2.id", target = "team2Id")
    })
    Match entityToModel(MatchEntity matchEntity);

    @Mappings({
            @Mapping(source = "tournamentId", target = "tournament.id"),
            @Mapping(source = "team1Id", target = "team1.id"),
            @Mapping(source = "team2Id", target = "team2.id")
    })
    MatchEntity modelToEntity(Match match);

    List<Match> entitiesToModels(List<MatchEntity> matchEntities);
    List<MatchEntity> modelsToEntities(List<Match> matches);
}
