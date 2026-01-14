package edu.fontysmaua.tournamentapi.mapper;

import edu.fontysmaua.tournamentapi.domain.User;
import edu.fontysmaua.tournamentapi.domain.UserDto;
import edu.fontysmaua.tournamentapi.persistence.entity.UserEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto entityToModel(UserEntity userEntity);

    User entityToUser(UserEntity userEntity);

    UserEntity modelToEntity(User user);

    List<UserDto> entitiesToModels(List<UserEntity> userEntities);

    List<User> entitiesToUsers(List<UserEntity> userEntities);

    List<UserEntity> modelsToEntities(List<User> users);
}
