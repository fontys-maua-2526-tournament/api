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
import java.util.UUID;

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

    @Column(name = "invite_code", unique = true)
    private String inviteCode;

    @ManyToOne
    @JoinColumn(name = "coach_id")
    private UserEntity coach;

    @ManyToMany
    @JoinTable(
            name = "team_members",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private List<UserEntity> members = new ArrayList<>();
    // Semantics in the dev branch call the field as users rather than members

    @ManyToMany
    @JoinTable(
            name = "team_tournament",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "tournament_id")
    )
    @Builder.Default
    private List<TournamentEntity> tournaments = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "team_organization",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "organization_id")
    )
    @Builder.Default
    private List<OrganizationEntity> organizations = new ArrayList<>();

    @PrePersist
    public void generateInviteCode() {
        if (this.inviteCode == null) {
            this.inviteCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}
