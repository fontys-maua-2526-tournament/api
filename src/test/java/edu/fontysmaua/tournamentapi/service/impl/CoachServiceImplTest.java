package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.Tournament;
import edu.fontysmaua.tournamentapi.domain.User;
import edu.fontysmaua.tournamentapi.domain.request.SaveCoachRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllCoachesResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetTournamentsByUserIdResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedCoachResponse;
import edu.fontysmaua.tournamentapi.enums.UserRole;
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
        userEntity.setUserRole(UserRole.COACH);

        user = new User();
        user.setId(userEntity.getId());
        user.setFirstName(userEntity.getFirstName());
        user.setEmail(userEntity.getEmail());
        user.setUserRole(UserRole.COACH);
    }

    // --- findAll() tests ---

    @Test
    void findAll_ShouldReturnOnlyCoaches_WhenRepositoryReturnsCoachEntities() {
        // Arrange
        when(userRepository.findAllByUserRole(UserRole.COACH)).thenReturn(List.of(userEntity));
        when(userMapper.entitiesToModels(anyList())).thenReturn(List.of(user));

        // Act
        GetAllCoachesResponse response = coachService.findAll();

        // Assert
        assertNotNull(response);
        assertNotNull(response.getCoaches());
        assertEquals(1, response.getCoaches().size());
        assertEquals(user, response.getCoaches().getFirst());
        assertEquals(UserRole.COACH, response.getCoaches().getFirst().getUserRole());

        verify(userRepository, times(1)).findAllByUserRole(UserRole.COACH);
        verify(userMapper, times(1)).entitiesToModels(anyList());
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoCoachesExist() {
        // Arrange
        when(userRepository.findAllByUserRole(UserRole.COACH)).thenReturn(Collections.emptyList());
        when(userMapper.entitiesToModels(anyList())).thenReturn(Collections.emptyList());

        // Act
        GetAllCoachesResponse response = coachService.findAll();

        // Assert
        assertNotNull(response);
        assertTrue(response.getCoaches().isEmpty());

        verify(userRepository, times(1)).findAllByUserRole(UserRole.COACH);
        verify(userMapper, times(1)).entitiesToModels(anyList());
    }

    @Test
    void findAll_ShouldFilterByUserRoleCoach() {
        // Arrange - create multiple coaches
        UserEntity coach1 = new UserEntity();
        coach1.setId(1L);
        coach1.setFirstName("Coach One");
        coach1.setUserRole(UserRole.COACH);

        UserEntity coach2 = new UserEntity();
        coach2.setId(2L);
        coach2.setFirstName("Coach Two");
        coach2.setUserRole(UserRole.COACH);

        User userCoach1 = new User();
        userCoach1.setId(1L);
        userCoach1.setFirstName("Coach One");
        userCoach1.setUserRole(UserRole.COACH);

        User userCoach2 = new User();
        userCoach2.setId(2L);
        userCoach2.setFirstName("Coach Two");
        userCoach2.setUserRole(UserRole.COACH);

        when(userRepository.findAllByUserRole(UserRole.COACH)).thenReturn(List.of(coach1, coach2));
        when(userMapper.entitiesToModels(anyList())).thenReturn(List.of(userCoach1, userCoach2));

        // Act
        GetAllCoachesResponse response = coachService.findAll();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getCoaches().size());
        assertTrue(response.getCoaches().stream().allMatch(c -> c.getUserRole() == UserRole.COACH));

        verify(userRepository, times(1)).findAllByUserRole(UserRole.COACH);
    }

    // --- findTournamentsByUserId() tests ---

    @Test
    void findTournamentsByUserId_ShouldReturnMappedTournaments_WhenCoachExists() {
        // Arrange
        Long userId = 1L;
        TournamentEntity tournamentEntity = new TournamentEntity();
        Tournament tournament = new Tournament();

        when(userRepository.findByIdAndUserRole(userId, UserRole.COACH)).thenReturn(Optional.of(userEntity));
        when(tournamentRepository.findAllByTeamsUsersId(userId)).thenReturn(List.of(tournamentEntity));
        when(tournamentMapper.entitiesToModels(anyList())).thenReturn(List.of(tournament));

        // Act
        GetTournamentsByUserIdResponse response = coachService.findTournamentsByUserId(userId);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getTournaments());
        assertEquals(1, response.getTournaments().size());
        assertEquals(tournament, response.getTournaments().getFirst());

        verify(userRepository, times(1)).findByIdAndUserRole(userId, UserRole.COACH);
        verify(tournamentRepository, times(1)).findAllByTeamsUsersId(userId);
        verify(tournamentMapper, times(1)).entitiesToModels(anyList());
    }

    @Test
    void findTournamentsByUserId_ShouldReturnEmptyList_WhenCoachHasNoTournaments() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findByIdAndUserRole(userId, UserRole.COACH)).thenReturn(Optional.of(userEntity));
        when(tournamentRepository.findAllByTeamsUsersId(userId)).thenReturn(Collections.emptyList());
        when(tournamentMapper.entitiesToModels(anyList())).thenReturn(Collections.emptyList());

        // Act
        GetTournamentsByUserIdResponse response = coachService.findTournamentsByUserId(userId);

        // Assert
        assertNotNull(response);
        assertTrue(response.getTournaments().isEmpty());

        verify(userRepository, times(1)).findByIdAndUserRole(userId, UserRole.COACH);
        verify(tournamentRepository, times(1)).findAllByTeamsUsersId(userId);
        verify(tournamentMapper, times(1)).entitiesToModels(anyList());
    }

    @Test
    void findTournamentsByUserId_ShouldThrowException_WhenUserIsNotCoach() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findByIdAndUserRole(userId, UserRole.COACH)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> coachService.findTournamentsByUserId(userId));

        assertEquals("Coach not found", exception.getMessage());
        verify(userRepository, times(1)).findByIdAndUserRole(userId, UserRole.COACH);
        verify(tournamentRepository, never()).findAllByTeamsUsersId(any());
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
    void create_ShouldSaveWithUserRoleCoach_WhenEmailDoesNotExist() {
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
        
        UserEntity capturedEntity = entityCaptor.getValue();
        assertEquals(request.getName(), capturedEntity.getFirstName());
        assertEquals(request.getEmail(), capturedEntity.getEmail());
        assertEquals(UserRole.COACH, capturedEntity.getUserRole());
    }

    @Test
    void create_ShouldAlwaysSetUserRoleToCoach() {
        // Arrange
        SaveCoachRequest request = new SaveCoachRequest(null, "Jane", "jane@example.com");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity entity = invocation.getArgument(0);
            entity.setId(2L);
            return entity;
        });
        when(userMapper.entityToModel(any(UserEntity.class))).thenReturn(user);

        // Act
        coachService.create(request);

        // Assert
        ArgumentCaptor<UserEntity> entityCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(entityCaptor.capture());
        
        assertEquals(UserRole.COACH, entityCaptor.getValue().getUserRole());
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
        verify(userRepository, never()).findByIdAndUserRole(any(), any());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void update_ShouldThrowException_WhenCoachNotFound() {
        // Arrange
        SaveCoachRequest request = new SaveCoachRequest(1L, "John", "john@example.com");
        when(userRepository.findByIdAndUserRole(request.getId(), UserRole.COACH)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> coachService.update(request));

        assertEquals("Coach not found", exception.getMessage());
        verify(userRepository, times(1)).findByIdAndUserRole(request.getId(), UserRole.COACH);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void update_ShouldThrowException_WhenUserExistsButIsNotCoach() {
        // Arrange
        SaveCoachRequest request = new SaveCoachRequest(1L, "John", "john@example.com");
        // User exists but with different role - findByIdAndUserRole returns empty
        when(userRepository.findByIdAndUserRole(request.getId(), UserRole.COACH)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> coachService.update(request));

        assertEquals("Coach not found", exception.getMessage());
        verify(userRepository, times(1)).findByIdAndUserRole(request.getId(), UserRole.COACH);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void update_ShouldUpdateAndReturnCoach_WhenCoachExists() {
        // Arrange
        SaveCoachRequest request = new SaveCoachRequest(1L, "Jane", "jane@example.com");
        when(userRepository.findByIdAndUserRole(request.getId(), UserRole.COACH)).thenReturn(Optional.of(userEntity));
        when(userRepository.existsByEmailAndIdNot(request.getEmail(), request.getId())).thenReturn(false);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.entityToModel(userEntity)).thenReturn(user);

        // Act
        SavedCoachResponse response = coachService.update(request);

        // Assert
        assertNotNull(response);
        assertEquals(user, response.getUser());
        assertEquals(request.getName(), userEntity.getFirstName());
        assertEquals(request.getEmail(), userEntity.getEmail());

        verify(userRepository, times(1)).findByIdAndUserRole(request.getId(), UserRole.COACH);
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    void update_ShouldThrowException_WhenNewEmailAlreadyExists() {
        // Arrange
        SaveCoachRequest request = new SaveCoachRequest(1L, "Jane", "existingemail@example.com");
        userEntity.setEmail("oldemail@example.com"); // Different from request email
        
        when(userRepository.findByIdAndUserRole(request.getId(), UserRole.COACH)).thenReturn(Optional.of(userEntity));
        when(userRepository.existsByEmailAndIdNot(request.getEmail(), request.getId())).thenReturn(true);

        // Act & Assert
        NameAlreadyExistsException exception = assertThrows(NameAlreadyExistsException.class,
                () -> coachService.update(request));

        assertEquals("Email already exists", exception.getReason());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void update_ShouldAllowSameEmail_WhenEmailNotChanged() {
        // Arrange
        String sameEmail = "john@example.com";
        SaveCoachRequest request = new SaveCoachRequest(1L, "John Updated", sameEmail);
        userEntity.setEmail(sameEmail);
        
        when(userRepository.findByIdAndUserRole(request.getId(), UserRole.COACH)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.entityToModel(userEntity)).thenReturn(user);

        // Act
        SavedCoachResponse response = coachService.update(request);

        // Assert
        assertNotNull(response);
        verify(userRepository, never()).existsByEmailAndIdNot(any(), any());
        verify(userRepository, times(1)).save(userEntity);
    }

    // --- delete() tests ---

    @Test
    void delete_ShouldThrowException_WhenIdIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> coachService.delete(null));

        assertEquals("Invalid coach id", exception.getMessage());
        verify(userRepository, never()).findByIdAndUserRole(any(), any());
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

        verify(userRepository, never()).findByIdAndUserRole(any(), any());
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void delete_ShouldThrowException_WhenUserIsNotCoach() {
        // Arrange
        Long id = 10L;
        when(userRepository.findByIdAndUserRole(id, UserRole.COACH)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> coachService.delete(id));

        assertEquals("Coach not found", exception.getMessage());
        verify(userRepository, times(1)).findByIdAndUserRole(id, UserRole.COACH);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void delete_ShouldCallRepositoryAndReturnId_WhenCoachExists() {
        // Arrange
        Long id = 10L;
        when(userRepository.findByIdAndUserRole(id, UserRole.COACH)).thenReturn(Optional.of(userEntity));

        // Act
        Long result = coachService.delete(id);

        // Assert
        assertEquals(id, result);

        verify(userRepository, times(1)).findByIdAndUserRole(id, UserRole.COACH);
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
