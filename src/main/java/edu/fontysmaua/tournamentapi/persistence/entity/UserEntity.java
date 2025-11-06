package edu.fontysmaua.tournamentapi.persistence.entity;

import edu.fontysmaua.tournamentapi.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private UserRole userRole;

    @ManyToMany
    @JoinTable(
            name = "user_team",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    private List<TeamEntity> teams = new ArrayList<>();

    @OneToMany(mappedBy = "organizer")
    private List<TournamentEntity> tournaments = new ArrayList<>();
}
