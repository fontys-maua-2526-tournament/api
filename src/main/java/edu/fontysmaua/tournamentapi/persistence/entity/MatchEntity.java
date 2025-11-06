package edu.fontysmaua.tournamentapi.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MatchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private TournamentEntity tournament;

    private Integer round;

    @ManyToOne
    @JoinColumn(name = "team1_id")
    private TeamEntity team1;

    @ManyToOne
    @JoinColumn(name = "team2_id")
    private TeamEntity team2;

    private Integer team1Score;
    private Integer team2Score;
}
