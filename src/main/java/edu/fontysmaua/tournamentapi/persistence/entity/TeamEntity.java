package edu.fontysmaua.tournamentapi.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "team")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Length(min = 2, max = 50)
    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "teams")
    private List<UserEntity> users  = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "team_tournament",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "tournament_id")
    )
    private List<TournamentEntity> tournaments = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "team_organization",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "organization_id")
    )
    private List<OrganizationEntity> organizations = new ArrayList<>();
}
