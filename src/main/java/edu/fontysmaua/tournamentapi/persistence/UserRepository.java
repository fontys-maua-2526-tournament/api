package edu.fontysmaua.tournamentapi.persistence;

import edu.fontysmaua.tournamentapi.enums.UserRole;
import edu.fontysmaua.tournamentapi.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);

    UserRole findUserRoleById(Long id);

    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findAllByUserRole(UserRole userRole);
}
