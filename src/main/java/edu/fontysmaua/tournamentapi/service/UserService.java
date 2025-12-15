package edu.fontysmaua.tournamentapi.service;

import edu.fontysmaua.tournamentapi.domain.UserDto;
import edu.fontysmaua.tournamentapi.domain.request.ChangePasswordUserRequest;
import edu.fontysmaua.tournamentapi.domain.request.LoginRequest;
import edu.fontysmaua.tournamentapi.domain.request.RegisterRequest;
import edu.fontysmaua.tournamentapi.domain.request.UpdateUserRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllUsersResponse;
import edu.fontysmaua.tournamentapi.enums.UserRole;

public interface UserService {
    GetAllUsersResponse findAll();

    GetAllUsersResponse findByRole(UserRole role);

    UserDto update(UpdateUserRequest request);

    boolean changePassword(ChangePasswordUserRequest request);

    void deleteUserById(Long id);

}
