package edu.fontysmaua.tournamentapi.domain.request;

import edu.fontysmaua.tournamentapi.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;

    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 2, max = 30)
    private String password;

    @NotBlank
    private String phoneNumber;
    @NotBlank
    private LocalDate dateOfBirth;

    @NotBlank
    private String userRole;
}
