package edu.fontysmaua.tournamentapi.persistence;

import edu.fontysmaua.tournamentapi.persistence.entity.TournamentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TournamentRepository extends JpaRepository<TournamentEntity, Long> {
    boolean existsByName(String name);
    
    List<TournamentEntity> findAllByTeamsUsersId(Long userId);
}
