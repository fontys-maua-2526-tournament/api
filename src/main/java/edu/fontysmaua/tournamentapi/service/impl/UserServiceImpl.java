package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.UserDto;
import edu.fontysmaua.tournamentapi.domain.request.ChangePasswordUserRequest;
import edu.fontysmaua.tournamentapi.domain.request.UpdateUserRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllUsersResponse;
import edu.fontysmaua.tournamentapi.enums.UserRole;
import edu.fontysmaua.tournamentapi.mapper.UserMapper;
import edu.fontysmaua.tournamentapi.persistence.UserRepository;
import edu.fontysmaua.tournamentapi.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public GetAllUsersResponse findAll() {
        return new GetAllUsersResponse(userMapper.entitiesToDtos(userRepository.findAll()));
    }

    @Override
    public GetAllUsersResponse findByRole(UserRole role) {
        return new GetAllUsersResponse(userMapper.entitiesToDtos(userRepository.findAllByUserRole((role))));
    }

    @Override
    public UserDto update(UpdateUserRequest request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("Id is required");
        }

        var existing = userRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Coach not found"));

        existing.setFirstName(request.getFirstName());
        existing.setLastName(request.getLastName());
        existing.setDateOfBirth(request.getDateOfBirth());
        existing.setPhoneNumber(request.getPhoneNumber());

        return userMapper.entityToDto(userRepository.save(existing));
    }

    @Override
    public boolean changePassword(ChangePasswordUserRequest request) {
        return false;
    }

    @Override
    public void deleteUserById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user id");
        }
        userRepository.deleteById(id);
    }
}
