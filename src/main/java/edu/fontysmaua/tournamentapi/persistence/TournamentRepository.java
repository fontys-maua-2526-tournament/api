package edu.fontysmaua.tournamentapi.persistence;

import edu.fontysmaua.tournamentapi.persistence.entity.TournamentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<TournamentEntity, Long> {
    boolean existsByName(String name);

    List<TournamentEntity> findAllByTeamsMembersId(Long memberId);
}
