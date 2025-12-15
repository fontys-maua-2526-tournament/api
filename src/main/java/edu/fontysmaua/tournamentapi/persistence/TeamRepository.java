package edu.fontysmaua.tournamentapi.persistence;

import edu.fontysmaua.tournamentapi.persistence.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<TeamEntity, Long> {
    Optional<TeamEntity> findByInviteCode(String inviteCode);
    
    List<TeamEntity> findAllByCoachId(Long coachId);
    
    boolean existsByInviteCode(String inviteCode);
}
