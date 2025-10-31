package edu.fontysmaua.tournamentapi.persistence;

import edu.fontysmaua.tournamentapi.persistence.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<TeamEntity, Long> {
}
