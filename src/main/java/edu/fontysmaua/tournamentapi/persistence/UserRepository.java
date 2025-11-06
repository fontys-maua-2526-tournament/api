package edu.fontysmaua.tournamentapi.persistence;

import edu.fontysmaua.tournamentapi.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);
}
