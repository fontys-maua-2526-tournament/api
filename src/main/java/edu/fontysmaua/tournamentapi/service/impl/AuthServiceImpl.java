package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.request.LoginRequest;
import edu.fontysmaua.tournamentapi.domain.request.RegisterRequest;
import edu.fontysmaua.tournamentapi.domain.response.AuthResponse;
import edu.fontysmaua.tournamentapi.enums.UserRole;
import edu.fontysmaua.tournamentapi.mapper.UserMapper;
import edu.fontysmaua.tournamentapi.persistence.UserRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.UserEntity;
import edu.fontysmaua.tournamentapi.security.UserPrincipal;
import edu.fontysmaua.tournamentapi.service.AuthService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new BadCredentialsException("Email already exists");
        }
        UserEntity toSave = new UserEntity();
        toSave.setFirstName(req.getFirstName());
        toSave.setLastName(req.getLastName());
        toSave.setEmail(req.getEmail());
        toSave.setPassword(passwordEncoder.encode(req.getPassword()));
        toSave.setDateOfBirth(req.getDateOfBirth());
        toSave.setPhoneNumber(req.getPhoneNumber());
        toSave.setUserRole(UserRole.valueOf(req.getUserRole()));

        UserEntity saved = userRepo.save(toSave);

        return getAuthResponse(saved);
    }

    public AuthResponse login(LoginRequest req) {
        UserEntity user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + req.getEmail()));
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return getAuthResponse(user);
    }

    @NotNull
    private AuthResponse getAuthResponse(UserEntity saved) {
        UserPrincipal userPrincipal = new UserPrincipal(userMapper.entityToModel(saved));
        String token = jwtService.generateToken(userPrincipal);

        AuthResponse res = new AuthResponse();
        res.setId(saved.getId());
        res.setToken(token);
        res.setEmail(saved.getEmail());
        res.setRole(saved.getUserRole().name());
        return res;
    }
}
