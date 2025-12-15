package edu.fontysmaua.tournamentapi.persistence.entity;

import edu.fontysmaua.tournamentapi.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "match")
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

    @ManyToOne
    @JoinColumn(name = "match1_id")
    private MatchEntity match1;

    @ManyToOne
    @JoinColumn(name = "match2_id")
    private MatchEntity match2;

    @Column(name ="team1_score")
    private Integer team1Score;

    @Column(name ="team2_score")
    private Integer team2Score;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;
}
