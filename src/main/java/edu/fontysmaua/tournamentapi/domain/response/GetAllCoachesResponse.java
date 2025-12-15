package edu.fontysmaua.tournamentapi.domain.response;

import edu.fontysmaua.tournamentapi.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllCoachesResponse {
    private List<User> coaches;
}

