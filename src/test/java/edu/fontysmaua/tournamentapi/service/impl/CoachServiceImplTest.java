package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.Team;
import edu.fontysmaua.tournamentapi.domain.Tournament;
import edu.fontysmaua.tournamentapi.domain.User;
import edu.fontysmaua.tournamentapi.domain.request.AddAthleteToTeamRequest;
import edu.fontysmaua.tournamentapi.domain.request.SaveCoachRequest;
import edu.fontysmaua.tournamentapi.domain.request.UpdateTeamRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllCoachesResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetTournamentsByUserIdResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedCoachResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedTeamResponse;
import edu.fontysmaua.tournamentapi.domain.response.TeamMemberResponse;
import edu.fontysmaua.tournamentapi.enums.UserRole;
import edu.fontysmaua.tournamentapi.mapper.TeamMapper;
import edu.fontysmaua.tournamentapi.mapper.TournamentMapper;
import edu.fontysmaua.tournamentapi.mapper.UserMapper;
import edu.fontysmaua.tournamentapi.persistence.TeamRepository;
import edu.fontysmaua.tournamentapi.persistence.TournamentRepository;
import edu.fontysmaua.tournamentapi.persistence.UserRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.TeamEntity;
import edu.fontysmaua.tournamentapi.persistence.entity.TournamentEntity;
import edu.fontysmaua.tournamentapi.persistence.entity.UserEntity;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
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
    private TeamMapper teamMapper;
    @Mock
    private TournamentMapper tournamentMapper;

    @InjectMocks
    private CoachServiceImpl coachService;

    private UserEntity coachEntity;
    private User coach;
    private UserEntity athleteEntity;
    private TeamEntity teamEntity;
    private Team team;

    @BeforeEach
    void setUp() {
        coachEntity = new UserEntity();
        coachEntity.setId(1L);
        coachEntity.setFirstName("John");
        coachEntity.setEmail("john@example.com");
        coachEntity.setUserRole(UserRole.COACH);

        coach = new User();
        coach.setId(coachEntity.getId());
        coach.setFirstName(coachEntity.getFirstName());
        coach.setEmail(coachEntity.getEmail());
        coach.setUserRole(UserRole.COACH);

        athleteEntity = new UserEntity();
        athleteEntity.setId(2L);
        athleteEntity.setFirstName("Young Athlete");
        athleteEntity.setEmail("young@example.com");
        athleteEntity.setUserRole(UserRole.ATHLETE);
        athleteEntity.setDateOfBirth(LocalDate.now().minusYears(15)); // 15 years old (underage)

        teamEntity = TeamEntity.builder()
                .id(1L)
                .name("Test Team")
                .inviteCode("ABC12345")
                .coach(coachEntity)
                .members(new ArrayList<>())
                .build();

        team = Team.builder()
                .id(1L)
                .name("Test Team")
                .inviteCode("ABC12345")
                .build();
    }

    // --- findAll() tests ---

    @Test
    void findAll_ShouldReturnOnlyCoaches_WhenRepositoryReturnsCoachEntities() {
        when(userRepository.findAllByUserRole(UserRole.COACH)).thenReturn(List.of(coachEntity));
        when(userMapper.entitiesToDtos(anyList())).thenReturn(List.of(coach));

        GetAllCoachesResponse response = coachService.findAll();

        assertNotNull(response);
        assertNotNull(response.getCoaches());
        assertEquals(1, response.getCoaches().size());
        assertEquals(UserRole.COACH, response.getCoaches().getFirst().getUserRole());

        verify(userRepository, times(1)).findAllByUserRole(UserRole.COACH);
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoCoachesExist() {
        when(userRepository.findAllByUserRole(UserRole.COACH)).thenReturn(Collections.emptyList());
        when(userMapper.entitiesToDtos(anyList())).thenReturn(Collections.emptyList());

        GetAllCoachesResponse response = coachService.findAll();

        assertNotNull(response);
        assertTrue(response.getCoaches().isEmpty());
    }

    // --- findTournamentsByUserId() tests ---

    @Test
    void findTournamentsByUserId_ShouldReturnMappedTournaments_WhenCoachExists() {
        Long userId = 1L;
        TournamentEntity tournamentEntity = new TournamentEntity();
        Tournament tournament = new Tournament();

        when(userRepository.findByIdAndUserRole(userId, UserRole.COACH)).thenReturn(Optional.of(coachEntity));
        when(tournamentRepository.findAllByTeamsMembersId(userId)).thenReturn(List.of(tournamentEntity));
        when(tournamentMapper.entitiesToModels(anyList())).thenReturn(List.of(tournament));

        GetTournamentsByUserIdResponse response = coachService.findTournamentsByUserId(userId);

        assertNotNull(response);
        assertEquals(1, response.getTournaments().size());
    }

    @Test
    void findTournamentsByUserId_ShouldThrowException_WhenUserIsNotCoach() {
        Long userId = 1L;
        when(userRepository.findByIdAndUserRole(userId, UserRole.COACH)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> coachService.findTournamentsByUserId(userId));
    }

    // --- create() tests ---

    @Test
    void create_ShouldThrowNameAlreadyExistsException_WhenEmailExists() {
        SaveCoachRequest request = new SaveCoachRequest(null, "John", "john@example.com");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> coachService.create(request));
    }

    @Test
    void create_ShouldSaveWithUserRoleCoach_WhenEmailDoesNotExist() {
        SaveCoachRequest request = new SaveCoachRequest(null, "John", "john@example.com");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(coachEntity);
        when(userMapper.entityToDto(coachEntity)).thenReturn(coach);

        SavedCoachResponse response = coachService.create(request);

        assertNotNull(response);
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        assertEquals(UserRole.COACH, captor.getValue().getUserRole());
    }

    // --- update() tests ---

    @Test
    void update_ShouldThrowException_WhenIdIsNull() {
        SaveCoachRequest request = new SaveCoachRequest(null, "John", "john@example.com");

        assertThrows(IllegalArgumentException.class, () -> coachService.update(request));
    }

    @Test
    void update_ShouldThrowException_WhenCoachNotFound() {
        SaveCoachRequest request = new SaveCoachRequest(1L, "John", "john@example.com");
        when(userRepository.findByIdAndUserRole(1L, UserRole.COACH)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> coachService.update(request));
    }

    // --- delete() tests ---

    @Test
    void delete_ShouldThrowException_WhenIdIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> coachService.delete(null));
        assertThrows(IllegalArgumentException.class, () -> coachService.delete(0L));
        assertThrows(IllegalArgumentException.class, () -> coachService.delete(-1L));
    }

    @Test
    void delete_ShouldDeleteCoach_WhenCoachExists() {
        when(userRepository.findByIdAndUserRole(1L, UserRole.COACH)).thenReturn(Optional.of(coachEntity));

        Long result = coachService.delete(1L);

        assertEquals(1L, result);
        verify(userRepository).deleteById(1L);
    }

    // --- addUnderageAthleteToTeam() tests ---

    @Test
    void addUnderageAthleteToTeam_ShouldThrowException_WhenCoachNotFound() {
        AddAthleteToTeamRequest request = new AddAthleteToTeamRequest(1L, 2L);
        when(userRepository.findByIdAndUserRole(1L, UserRole.COACH)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> coachService.addUnderageAthleteToTeam(request, 1L));

        assertEquals("Coach not found", exception.getMessage());
    }

    @Test
    void addUnderageAthleteToTeam_ShouldThrowException_WhenTeamNotFound() {
        AddAthleteToTeamRequest request = new AddAthleteToTeamRequest(1L, 2L);
        when(userRepository.findByIdAndUserRole(1L, UserRole.COACH)).thenReturn(Optional.of(coachEntity));
        when(teamRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> coachService.addUnderageAthleteToTeam(request, 1L));

        assertEquals("Team not found", exception.getMessage());
    }

    @Test
    void addUnderageAthleteToTeam_ShouldThrowException_WhenCoachDoesNotOwnTeam() {
        AddAthleteToTeamRequest request = new AddAthleteToTeamRequest(1L, 2L);
        
        UserEntity otherCoach = new UserEntity();
        otherCoach.setId(99L);
        teamEntity.setCoach(otherCoach);

        when(userRepository.findByIdAndUserRole(1L, UserRole.COACH)).thenReturn(Optional.of(coachEntity));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(teamEntity));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> coachService.addUnderageAthleteToTeam(request, 1L));

        assertEquals("You are not the coach of this team", exception.getMessage());
    }

    @Test
    void addUnderageAthleteToTeam_ShouldThrowException_WhenAthleteNotFound() {
        AddAthleteToTeamRequest request = new AddAthleteToTeamRequest(1L, 2L);
        
        when(userRepository.findByIdAndUserRole(1L, UserRole.COACH)).thenReturn(Optional.of(coachEntity));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(teamEntity));
        when(userRepository.findByIdAndUserRole(2L, UserRole.ATHLETE)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> coachService.addUnderageAthleteToTeam(request, 1L));

        assertEquals("Athlete not found", exception.getMessage());
    }

    @Test
    void addUnderageAthleteToTeam_ShouldThrowException_WhenAthleteIsAdult() {
        AddAthleteToTeamRequest request = new AddAthleteToTeamRequest(1L, 2L);
        
        // Make athlete an adult (20 years old)
        athleteEntity.setDateOfBirth(LocalDate.now().minusYears(20));

        when(userRepository.findByIdAndUserRole(1L, UserRole.COACH)).thenReturn(Optional.of(coachEntity));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(teamEntity));
        when(userRepository.findByIdAndUserRole(2L, UserRole.ATHLETE)).thenReturn(Optional.of(athleteEntity));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> coachService.addUnderageAthleteToTeam(request, 1L));

        assertEquals("Athlete is not underage. Adult athletes must join via invite code.", exception.getMessage());
    }

    @Test
    void addUnderageAthleteToTeam_ShouldThrowException_WhenAthleteAlreadyInTeam() {
        AddAthleteToTeamRequest request = new AddAthleteToTeamRequest(1L, 2L);
        
        teamEntity.getMembers().add(athleteEntity);

        when(userRepository.findByIdAndUserRole(1L, UserRole.COACH)).thenReturn(Optional.of(coachEntity));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(teamEntity));
        when(userRepository.findByIdAndUserRole(2L, UserRole.ATHLETE)).thenReturn(Optional.of(athleteEntity));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> coachService.addUnderageAthleteToTeam(request, 1L));

        assertEquals("Athlete is already a member of this team", exception.getMessage());
    }

    @Test
    void addUnderageAthleteToTeam_ShouldAddAthlete_WhenAllConditionsMet() {
        AddAthleteToTeamRequest request = new AddAthleteToTeamRequest(1L, 2L);
        
        when(userRepository.findByIdAndUserRole(1L, UserRole.COACH)).thenReturn(Optional.of(coachEntity));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(teamEntity));
        when(userRepository.findByIdAndUserRole(2L, UserRole.ATHLETE)).thenReturn(Optional.of(athleteEntity));
        when(teamRepository.save(any(TeamEntity.class))).thenReturn(teamEntity);
        when(teamMapper.entityToModel(teamEntity)).thenReturn(team);

        TeamMemberResponse response = coachService.addUnderageAthleteToTeam(request, 1L);

        assertNotNull(response);
        assertEquals("Underage athlete added successfully", response.getMessage());
        assertTrue(teamEntity.getMembers().contains(athleteEntity));
        verify(teamRepository).save(teamEntity);
    }

    // --- updateTeam() tests ---

    @Test
    void updateTeam_ShouldThrowException_WhenCoachNotFound() {
        UpdateTeamRequest request = new UpdateTeamRequest(1L, "New Name");
        when(userRepository.findByIdAndUserRole(1L, UserRole.COACH)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> coachService.updateTeam(request, 1L));

        assertEquals("Coach not found", exception.getMessage());
    }

    @Test
    void updateTeam_ShouldThrowException_WhenTeamNotFound() {
        UpdateTeamRequest request = new UpdateTeamRequest(1L, "New Name");
        when(userRepository.findByIdAndUserRole(1L, UserRole.COACH)).thenReturn(Optional.of(coachEntity));
        when(teamRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> coachService.updateTeam(request, 1L));

        assertEquals("Team not found", exception.getMessage());
    }

    @Test
    void updateTeam_ShouldThrowException_WhenCoachDoesNotOwnTeam() {
        UpdateTeamRequest request = new UpdateTeamRequest(1L, "New Name");
        
        UserEntity otherCoach = new UserEntity();
        otherCoach.setId(99L);
        teamEntity.setCoach(otherCoach);

        when(userRepository.findByIdAndUserRole(1L, UserRole.COACH)).thenReturn(Optional.of(coachEntity));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(teamEntity));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> coachService.updateTeam(request, 1L));

        assertEquals("You are not the coach of this team", exception.getMessage());
    }

    @Test
    void updateTeam_ShouldUpdateTeam_WhenCoachOwnsTeam() {
        UpdateTeamRequest request = new UpdateTeamRequest(1L, "Updated Team Name");
        
        when(userRepository.findByIdAndUserRole(1L, UserRole.COACH)).thenReturn(Optional.of(coachEntity));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(teamEntity));
        when(teamRepository.save(any(TeamEntity.class))).thenReturn(teamEntity);
        when(teamMapper.entityToModel(teamEntity)).thenReturn(team);

        SavedTeamResponse response = coachService.updateTeam(request, 1L);

        assertNotNull(response);
        assertEquals("Updated Team Name", teamEntity.getName());
        verify(teamRepository).save(teamEntity);
    }

    // --- disbandTeam() tests ---

    @Test
    void disbandTeam_ShouldThrowException_WhenTeamDoesNotExist() {
        when(teamRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> coachService.disbandTeam(1L));
    }

    @Test
    void disbandTeam_ShouldDeleteTeam_WhenTeamExists() {
        when(teamRepository.existsById(1L)).thenReturn(true);

        coachService.disbandTeam(1L);

        verify(teamRepository).deleteById(1L);
    }

    // --- registerTeamInTournament() tests ---

    @Test
    void registerTeamInTournament_ShouldThrowException_WhenTeamNotFound() {
        when(teamRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> coachService.registerTeamInTournament(1L, 2L));
    }

    @Test
    void registerTeamInTournament_ShouldThrowException_WhenTournamentNotFound() {
        when(teamRepository.existsById(1L)).thenReturn(true);
        when(tournamentRepository.existsById(2L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> coachService.registerTeamInTournament(1L, 2L));
    }

    @Test
    void registerTeamInTournament_ShouldSucceed_WhenBothExist() {
        when(teamRepository.existsById(1L)).thenReturn(true);
        when(tournamentRepository.existsById(2L)).thenReturn(true);

        assertDoesNotThrow(() -> coachService.registerTeamInTournament(1L, 2L));
    }

    // --- withdrawTeamFromTournament() tests ---

    @Test
    void withdrawTeamFromTournament_ShouldThrowException_WhenTeamNotFound() {
        when(teamRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> coachService.withdrawTeamFromTournament(1L, 2L));
    }

    @Test
    void withdrawTeamFromTournament_ShouldSucceed_WhenBothExist() {
        when(teamRepository.existsById(1L)).thenReturn(true);
        when(tournamentRepository.existsById(2L)).thenReturn(true);

        assertDoesNotThrow(() -> coachService.withdrawTeamFromTournament(1L, 2L));
    }
}
