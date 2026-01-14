package edu.fontysmaua.tournamentapi.persistence.entity;

import edu.fontysmaua.tournamentapi.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @ManyToMany(mappedBy = "members")
    @Builder.Default
    private List<TeamEntity> teams = new ArrayList<>();

    @OneToMany(mappedBy = "organizer")
    private List<TournamentEntity> tournaments = new ArrayList<>();

    public boolean isUnderage() {
        if (dateOfBirth == null) {
            return false;
        }
        return dateOfBirth.plusYears(18).isAfter(LocalDate.now());
    }
}
