package edu.fontysmaua.tournamentapi.service;

import edu.fontysmaua.tournamentapi.domain.User;
import edu.fontysmaua.tournamentapi.domain.request.ChangePasswordUserRequest;
import edu.fontysmaua.tournamentapi.domain.request.LoginRequest;
import edu.fontysmaua.tournamentapi.domain.request.RegisterRequest;
import edu.fontysmaua.tournamentapi.domain.request.UpdateUserRequest;

public interface UserService {
    User getUserById(Long id);

    /// Both register and login are currently void which is wrong, at least for login it needs to return a JWT token
    ///
    /// @param request The register request
    ///
    /// @return void
    void register(RegisterRequest request);

    void login(LoginRequest request);

    boolean update(UpdateUserRequest request);

    boolean changePassword(ChangePasswordUserRequest request);

    void deleteUserById(Long id);
}
