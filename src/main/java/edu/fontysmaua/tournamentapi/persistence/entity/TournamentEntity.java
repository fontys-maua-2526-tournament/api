package edu.fontysmaua.tournamentapi.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tournament")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TournamentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Length(min = 2, max = 50)
    @Column(name = "name")
    private String name;

    @NotBlank
    @Length(min = 2, max = 255)
    @Column(name = "address")
    private String address;

    @NotNull
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @NotNull
    @Column(name = "end_time")
    private LocalDateTime endTime;

    @ManyToMany(mappedBy = "tournaments")
    private List<TeamEntity> teams = new ArrayList<>();

    @OneToMany(mappedBy = "tournament")
    private List<MatchEntity> matches = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private UserEntity organizer;
}
