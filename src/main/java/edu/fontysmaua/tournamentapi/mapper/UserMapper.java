package edu.fontysmaua.tournamentapi.mapper;

import edu.fontysmaua.tournamentapi.domain.User;
import edu.fontysmaua.tournamentapi.domain.UserDto;
import edu.fontysmaua.tournamentapi.persistence.entity.UserEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto entityToDto(UserEntity userEntity);
    User entityToModel(UserEntity userEntity);
    UserEntity modelToEntity(User user);

    List<User> entitiesToModels(List<UserEntity> userEntities);
    List<UserDto> entitiesToDtos(List<UserEntity> userEntities);
    List<UserEntity> modelsToEntities(List<User> users);
}
