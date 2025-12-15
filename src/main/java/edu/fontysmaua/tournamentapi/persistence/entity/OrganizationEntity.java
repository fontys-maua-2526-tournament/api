package edu.fontysmaua.tournamentapi.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "organization")
public class OrganizationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String name;
    @ManyToMany(mappedBy = "organizations")
    private List<TeamEntity> teams  = new ArrayList<>();

    // Many organizations can have one parent
    // MAUA semester 3 and MAUA semester 4 have one parent (MAUA)
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private OrganizationEntity parentOrganization;

    // One parent can have many children
    @OneToMany(mappedBy = "parentOrganization")
    private List<OrganizationEntity> childrenOrganization = new ArrayList<>();
}
