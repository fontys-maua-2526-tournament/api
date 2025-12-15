package edu.fontysmaua.tournamentapi.controller;

import edu.fontysmaua.tournamentapi.domain.UserDto;
import edu.fontysmaua.tournamentapi.domain.request.RegisterRequest;
import edu.fontysmaua.tournamentapi.domain.request.UpdateUserRequest;
import edu.fontysmaua.tournamentapi.domain.response.AuthResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetAllUsersResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetTournamentsByUserIdResponse;
import edu.fontysmaua.tournamentapi.enums.UserRole;
import edu.fontysmaua.tournamentapi.service.AuthService;
import edu.fontysmaua.tournamentapi.service.TournamentService;
import edu.fontysmaua.tournamentapi.service.UserService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coaches")
@RequiredArgsConstructor
public class CoachController {
    private final UserService userService;
    private final TournamentService tournamentService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<GetAllUsersResponse> findAll() {
        return ResponseEntity.ok(userService.findByRole(UserRole.COACH));
    }

    @GetMapping("{id}/tournaments")
    public ResponseEntity<GetTournamentsByUserIdResponse> getTournamentsByUserId(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(tournamentService.getByUserId(id));
    }

    @PostMapping("/create")
    public ResponseEntity<AuthResponse> create(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        if (!id.equals(request.getId())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(userService.update(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable @Positive Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
