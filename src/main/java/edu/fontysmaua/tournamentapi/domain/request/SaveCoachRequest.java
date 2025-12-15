package edu.fontysmaua.tournamentapi.domain.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveCoachRequest {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;
}

