package edu.fontysmaua.tournamentapi.service;

import edu.fontysmaua.tournamentapi.domain.request.LoginRequest;
import edu.fontysmaua.tournamentapi.domain.request.RegisterRequest;
import edu.fontysmaua.tournamentapi.domain.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest req);

    AuthResponse login(LoginRequest req);
}
