package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.Tournament;
import edu.fontysmaua.tournamentapi.domain.User;
import edu.fontysmaua.tournamentapi.domain.request.SaveCoachRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllCoachesResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetTournamentsByUserIdResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedCoachResponse;
import edu.fontysmaua.tournamentapi.exception.NameAlreadyExistsException;
import edu.fontysmaua.tournamentapi.mapper.TournamentMapper;
import edu.fontysmaua.tournamentapi.mapper.UserMapper;
import edu.fontysmaua.tournamentapi.persistence.TeamRepository;
import edu.fontysmaua.tournamentapi.persistence.TournamentRepository;
import edu.fontysmaua.tournamentapi.persistence.UserRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.TournamentEntity;
import edu.fontysmaua.tournamentapi.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoachServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private TournamentMapper tournamentMapper;

    @InjectMocks
    private CoachServiceImpl coachService;

    private UserEntity userEntity;
    private User user;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setFirstName("John");
        userEntity.setEmail("john@example.com");

        user = new User();
        user.setId(userEntity.getId());
        user.setFirstName(userEntity.getFirstName());
        user.setEmail(userEntity.getEmail());
    }

    // --- findAll() tests ---

    @Test
    void findAll_ShouldReturnMappedCoaches_WhenRepositoryReturnsEntities() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of(userEntity));
        when(userMapper.entitiesToModels(anyList())).thenReturn(List.of(user));

        // Act
        GetAllCoachesResponse response = coachService.findAll();

        // Assert
        assertNotNull(response);
        assertNotNull(response.getCoaches());
        assertEquals(1, response.getCoaches().size());
        assertEquals(user, response.getCoaches().getFirst());

        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).entitiesToModels(anyList());
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenRepositoryReturnsEmpty() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        when(userMapper.entitiesToModels(anyList())).thenReturn(Collections.emptyList());

        // Act
        GetAllCoachesResponse response = coachService.findAll();

        // Assert
        assertNotNull(response);
        assertTrue(response.getCoaches().isEmpty());

        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).entitiesToModels(anyList());
    }

    // --- findTournamentsByUserId() tests ---

    @Test
    void findTournamentsByUserId_ShouldReturnMappedTournaments_WhenRepositoryReturnsEntities() {
        // Arrange
        Long userId = 1L;
        TournamentEntity tournamentEntity = new TournamentEntity();
        Tournament tournament = new Tournament();

        when(tournamentRepository.findAllByTeamsUsersId(userId)).thenReturn(List.of(tournamentEntity));
        when(tournamentMapper.entitiesToModels(anyList())).thenReturn(List.of(tournament));

        // Act
        GetTournamentsByUserIdResponse response = coachService.findTournamentsByUserId(userId);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getTournaments());
        assertEquals(1, response.getTournaments().size());
        assertEquals(tournament, response.getTournaments().getFirst());

        verify(tournamentRepository, times(1)).findAllByTeamsUsersId(userId);
        verify(tournamentMapper, times(1)).entitiesToModels(anyList());
    }

    @Test
    void findTournamentsByUserId_ShouldReturnEmptyList_WhenNoTournamentsFound() {
        // Arrange
        Long userId = 1L;

        when(tournamentRepository.findAllByTeamsUsersId(userId)).thenReturn(Collections.emptyList());
        when(tournamentMapper.entitiesToModels(anyList())).thenReturn(Collections.emptyList());

        // Act
        GetTournamentsByUserIdResponse response = coachService.findTournamentsByUserId(userId);

        // Assert
        assertNotNull(response);
        assertTrue(response.getTournaments().isEmpty());

        verify(tournamentRepository, times(1)).findAllByTeamsUsersId(userId);
        verify(tournamentMapper, times(1)).entitiesToModels(anyList());
    }

    // --- create() tests ---

    @Test
    void create_ShouldThrowNameAlreadyExistsException_WhenEmailExists() {
        // Arrange
        SaveCoachRequest request = new SaveCoachRequest(null, "John", "john@example.com");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // Act & Assert
        NameAlreadyExistsException exception = assertThrows(NameAlreadyExistsException.class,
                () -> coachService.create(request));

        assertEquals("Email already exists", exception.getReason());
        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void create_ShouldSaveAndReturnCoach_WhenEmailDoesNotExist() {
        // Arrange
        SaveCoachRequest request = new SaveCoachRequest(null, "John", "john@example.com");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(userMapper.entityToModel(userEntity)).thenReturn(user);

        // Act
        SavedCoachResponse response = coachService.create(request);

        // Assert
        assertNotNull(response);
        assertEquals(user, response.getUser());

        ArgumentCaptor<UserEntity> entityCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository, times(1)).save(entityCaptor.capture());
        assertEquals(request.getName(), entityCaptor.getValue().getFirstName());
        assertEquals(request.getEmail(), entityCaptor.getValue().getEmail());
    }

    // --- update() tests ---

    @Test
    void update_ShouldThrowException_WhenIdIsNull() {
        // Arrange
        SaveCoachRequest request = new SaveCoachRequest(null, "John", "john@example.com");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> coachService.update(request));

        assertEquals("Id is required", exception.getMessage());
        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void update_ShouldThrowException_WhenCoachNotFound() {
        // Arrange
        SaveCoachRequest request = new SaveCoachRequest(1L, "John", "john@example.com");
        when(userRepository.findById(request.getId())).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> coachService.update(request));

        assertEquals("Coach not found", exception.getMessage());
        verify(userRepository, times(1)).findById(request.getId());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void update_ShouldUpdateAndReturnCoach_WhenCoachExists() {
        // Arrange
        SaveCoachRequest request = new SaveCoachRequest(1L, "Jane", "jane@example.com");
        when(userRepository.findById(request.getId())).thenReturn(Optional.of(userEntity));
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.entityToModel(userEntity)).thenReturn(user);

        // Act
        SavedCoachResponse response = coachService.update(request);

        // Assert
        assertNotNull(response);
        assertEquals(user, response.getUser());
        assertEquals(request.getName(), userEntity.getFirstName());
        assertEquals(request.getEmail(), userEntity.getEmail());

        verify(userRepository, times(1)).findById(request.getId());
        verify(userRepository, times(1)).save(userEntity);
    }

    // --- delete() tests ---

    @Test
    void delete_ShouldThrowException_WhenIdIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> coachService.delete(null));

        assertEquals("Invalid coach id", exception.getMessage());
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void delete_ShouldThrowException_WhenIdIsZeroOrNegative() {
        // Act & Assert
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class,
                () -> coachService.delete(0L));
        assertEquals("Invalid coach id", exception1.getMessage());

        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class,
                () -> coachService.delete(-5L));
        assertEquals("Invalid coach id", exception2.getMessage());

        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void delete_ShouldCallRepositoryAndReturnId_WhenIdIsValid() {
        // Arrange
        Long id = 10L;

        // Act
        Long result = coachService.delete(id);

        // Assert
        assertEquals(id, result);

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(userRepository, times(1)).deleteById(idCaptor.capture());
        assertEquals(id, idCaptor.getValue());
    }

    // --- disbandTeam() tests ---

    @Test
    void disbandTeam_ShouldThrowException_WhenTeamDoesNotExist() {
        // Arrange
        Long teamId = 1L;
        when(teamRepository.existsById(teamId)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> coachService.disbandTeam(teamId));

        assertEquals("Team does not exist", exception.getMessage());
        verify(teamRepository, times(1)).existsById(teamId);
        verify(teamRepository, never()).deleteById(any());
    }

    @Test
    void disbandTeam_ShouldDeleteTeam_WhenTeamExists() {
        // Arrange
        Long teamId = 1L;
        when(teamRepository.existsById(teamId)).thenReturn(true);

        // Act
        coachService.disbandTeam(teamId);

        // Assert
        verify(teamRepository, times(1)).existsById(teamId);
        verify(teamRepository, times(1)).deleteById(teamId);
    }

    // --- registerTeamInTournament() tests ---

    @Test
    void registerTeamInTournament_ShouldThrowException_WhenTeamNotFound() {
        // Arrange
        Long teamId = 1L;
        Long tournamentId = 2L;
        when(teamRepository.existsById(teamId)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> coachService.registerTeamInTournament(teamId, tournamentId));

        assertEquals("Team not found", exception.getMessage());
        verify(teamRepository, times(1)).existsById(teamId);
        verify(tournamentRepository, never()).existsById(any());
    }

    @Test
    void registerTeamInTournament_ShouldThrowException_WhenTournamentNotFound() {
        // Arrange
        Long teamId = 1L;
        Long tournamentId = 2L;
        when(teamRepository.existsById(teamId)).thenReturn(true);
        when(tournamentRepository.existsById(tournamentId)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> coachService.registerTeamInTournament(teamId, tournamentId));

        assertEquals("Tournament not found", exception.getMessage());
        verify(teamRepository, times(1)).existsById(teamId);
        verify(tournamentRepository, times(1)).existsById(tournamentId);
    }

    @Test
    void registerTeamInTournament_ShouldSucceed_WhenBothTeamAndTournamentExist() {
        // Arrange
        Long teamId = 1L;
        Long tournamentId = 2L;
        when(teamRepository.existsById(teamId)).thenReturn(true);
        when(tournamentRepository.existsById(tournamentId)).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> coachService.registerTeamInTournament(teamId, tournamentId));

        verify(teamRepository, times(1)).existsById(teamId);
        verify(tournamentRepository, times(1)).existsById(tournamentId);
    }

    // --- withdrawTeamFromTournament() tests ---

    @Test
    void withdrawTeamFromTournament_ShouldThrowException_WhenTeamNotFound() {
        // Arrange
        Long teamId = 1L;
        Long tournamentId = 2L;
        when(teamRepository.existsById(teamId)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> coachService.withdrawTeamFromTournament(teamId, tournamentId));

        assertEquals("Team not found", exception.getMessage());
        verify(teamRepository, times(1)).existsById(teamId);
        verify(tournamentRepository, never()).existsById(any());
    }

    @Test
    void withdrawTeamFromTournament_ShouldThrowException_WhenTournamentNotFound() {
        // Arrange
        Long teamId = 1L;
        Long tournamentId = 2L;
        when(teamRepository.existsById(teamId)).thenReturn(true);
        when(tournamentRepository.existsById(tournamentId)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> coachService.withdrawTeamFromTournament(teamId, tournamentId));

        assertEquals("Tournament not found", exception.getMessage());
        verify(teamRepository, times(1)).existsById(teamId);
        verify(tournamentRepository, times(1)).existsById(tournamentId);
    }

    @Test
    void withdrawTeamFromTournament_ShouldSucceed_WhenBothTeamAndTournamentExist() {
        // Arrange
        Long teamId = 1L;
        Long tournamentId = 2L;
        when(teamRepository.existsById(teamId)).thenReturn(true);
        when(tournamentRepository.existsById(tournamentId)).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> coachService.withdrawTeamFromTournament(teamId, tournamentId));

        verify(teamRepository, times(1)).existsById(teamId);
        verify(tournamentRepository, times(1)).existsById(tournamentId);
    }
}