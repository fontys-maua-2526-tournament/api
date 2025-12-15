package edu.fontysmaua.tournamentapi.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTeamRequest {
    @NotNull
    @Positive
    private Long id;

    @NotBlank
    @Length(min = 2, max = 50)
    private String name;
}

