package edu.fontysmaua.tournamentapi.domain.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Size(min=2, max=30)
    private String password;
}
