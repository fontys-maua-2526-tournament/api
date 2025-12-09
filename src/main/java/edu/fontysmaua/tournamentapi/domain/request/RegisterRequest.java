package edu.fontysmaua.tournamentapi.domain.request;

import edu.fontysmaua.tournamentapi.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String userRole;
}
