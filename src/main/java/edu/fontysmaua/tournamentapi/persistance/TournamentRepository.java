package edu.fontysmaua.tournamentapi.persistance;

import edu.fontysmaua.tournamentapi.persistance.entity.TournamentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentRepository extends JpaRepository<TournamentEntity, Long> {
    boolean existsByName(String name);
}