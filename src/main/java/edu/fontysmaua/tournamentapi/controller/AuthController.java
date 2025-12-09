package edu.fontysmaua.tournamentapi.controller;

import edu.fontysmaua.tournamentapi.domain.request.LoginRequest;
import edu.fontysmaua.tournamentapi.domain.request.RegisterRequest;
import edu.fontysmaua.tournamentapi.domain.response.AuthResponse;
import edu.fontysmaua.tournamentapi.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest req){
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req){
        return ResponseEntity.ok(authService.login(req));
    }
}
