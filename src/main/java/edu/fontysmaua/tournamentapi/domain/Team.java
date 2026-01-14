package edu.fontysmaua.tournamentapi.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    private Long id;
    private String name;
    private String inviteCode;
    private User coach;
    @Builder.Default
    private List<User> members = new ArrayList<>();
}
