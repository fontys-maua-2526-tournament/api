package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.Team;
import edu.fontysmaua.tournamentapi.domain.request.JoinTeamRequest;
import edu.fontysmaua.tournamentapi.domain.response.TeamMemberResponse;
import edu.fontysmaua.tournamentapi.enums.UserRole;
import edu.fontysmaua.tournamentapi.mapper.TeamMapper;
import edu.fontysmaua.tournamentapi.persistence.TeamRepository;
import edu.fontysmaua.tournamentapi.persistence.UserRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.TeamEntity;
import edu.fontysmaua.tournamentapi.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AthleteServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private TeamMapper teamMapper;

    @InjectMocks
    private AthleteServiceImpl athleteService;

    private UserEntity adultAthleteEntity;
    private UserEntity underageAthleteEntity;
    private TeamEntity teamEntity;
    private Team team;

    @BeforeEach
    void setUp() {
        // Adult athlete (20 years old)
        adultAthleteEntity = new UserEntity();
        adultAthleteEntity.setId(1L);
        adultAthleteEntity.setFirstName("Adult Athlete");
        adultAthleteEntity.setEmail("adult@example.com");
        adultAthleteEntity.setUserRole(UserRole.ATHLETE);
        adultAthleteEntity.setDateOfBirth(LocalDate.now().minusYears(20));

        // Underage athlete (15 years old)
        underageAthleteEntity = new UserEntity();
        underageAthleteEntity.setId(2L);
        underageAthleteEntity.setFirstName("Young Athlete");
        underageAthleteEntity.setEmail("young@example.com");
        underageAthleteEntity.setUserRole(UserRole.ATHLETE);
        underageAthleteEntity.setDateOfBirth(LocalDate.now().minusYears(15));

        teamEntity = TeamEntity.builder()
                .id(1L)
                .name("Test Team")
                .inviteCode("ABC12345")
                .members(new ArrayList<>())
                .build();

        team = Team.builder()
                .id(1L)
                .name("Test Team")
                .inviteCode("ABC12345")
                .build();
    }

    // --- joinTeamViaInvite() tests ---

    @Test
    void joinTeamViaInvite_ShouldThrowException_WhenAthleteNotFound() {
        JoinTeamRequest request = new JoinTeamRequest("ABC12345", 1L);
        when(userRepository.findByIdAndUserRole(1L, UserRole.ATHLETE)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> athleteService.joinTeamViaInvite(request));

        assertEquals("Athlete not found", exception.getMessage());
        verify(teamRepository, never()).findByInviteCode(any());
    }

    @Test
    void joinTeamViaInvite_ShouldThrowException_WhenAthleteIsUnderage() {
        JoinTeamRequest request = new JoinTeamRequest("ABC12345", 2L);
        when(userRepository.findByIdAndUserRole(2L, UserRole.ATHLETE)).thenReturn(Optional.of(underageAthleteEntity));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> athleteService.joinTeamViaInvite(request));

        assertEquals("Underage athletes cannot join via invite code. Please ask your coach to add you to the team.", exception.getMessage());
        verify(teamRepository, never()).findByInviteCode(any());
    }

    @Test
    void joinTeamViaInvite_ShouldThrowException_WhenInviteCodeIsInvalid() {
        JoinTeamRequest request = new JoinTeamRequest("INVALID", 1L);
        when(userRepository.findByIdAndUserRole(1L, UserRole.ATHLETE)).thenReturn(Optional.of(adultAthleteEntity));
        when(teamRepository.findByInviteCode("INVALID")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> athleteService.joinTeamViaInvite(request));

        assertEquals("Invalid invite code", exception.getMessage());
    }

    @Test
    void joinTeamViaInvite_ShouldThrowException_WhenAthleteAlreadyInTeam() {
        JoinTeamRequest request = new JoinTeamRequest("ABC12345", 1L);
        teamEntity.getMembers().add(adultAthleteEntity);

        when(userRepository.findByIdAndUserRole(1L, UserRole.ATHLETE)).thenReturn(Optional.of(adultAthleteEntity));
        when(teamRepository.findByInviteCode("ABC12345")).thenReturn(Optional.of(teamEntity));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> athleteService.joinTeamViaInvite(request));

        assertEquals("You are already a member of this team", exception.getMessage());
        verify(teamRepository, never()).save(any());
    }

    @Test
    void joinTeamViaInvite_ShouldAddAdultAthlete_WhenAllConditionsMet() {
        JoinTeamRequest request = new JoinTeamRequest("ABC12345", 1L);

        when(userRepository.findByIdAndUserRole(1L, UserRole.ATHLETE)).thenReturn(Optional.of(adultAthleteEntity));
        when(teamRepository.findByInviteCode("ABC12345")).thenReturn(Optional.of(teamEntity));
        when(teamRepository.save(any(TeamEntity.class))).thenReturn(teamEntity);
        when(teamMapper.entityToModel(teamEntity)).thenReturn(team);

        TeamMemberResponse response = athleteService.joinTeamViaInvite(request);

        assertNotNull(response);
        assertEquals("Successfully joined the team", response.getMessage());
        assertTrue(teamEntity.getMembers().contains(adultAthleteEntity));
        verify(teamRepository).save(teamEntity);
    }

    @Test
    void joinTeamViaInvite_ShouldWorkWithExactly18YearsOldAthlete() {
        // Athlete who just turned 18 today
        UserEntity justTurned18Entity = new UserEntity();
        justTurned18Entity.setId(3L);
        justTurned18Entity.setFirstName("Just 18");
        justTurned18Entity.setEmail("just18@example.com");
        justTurned18Entity.setUserRole(UserRole.ATHLETE);
        justTurned18Entity.setDateOfBirth(LocalDate.now().minusYears(18));

        JoinTeamRequest request = new JoinTeamRequest("ABC12345", 3L);

        when(userRepository.findByIdAndUserRole(3L, UserRole.ATHLETE)).thenReturn(Optional.of(justTurned18Entity));
        when(teamRepository.findByInviteCode("ABC12345")).thenReturn(Optional.of(teamEntity));
        when(teamRepository.save(any(TeamEntity.class))).thenReturn(teamEntity);
        when(teamMapper.entityToModel(teamEntity)).thenReturn(team);

        TeamMemberResponse response = athleteService.joinTeamViaInvite(request);

        assertNotNull(response);
        assertEquals("Successfully joined the team", response.getMessage());
    }

    @Test
    void joinTeamViaInvite_ShouldRejectAthleteDayBeforeTurning18() {
        // Athlete who will turn 18 tomorrow
        UserEntity almostAdultEntity = new UserEntity();
        almostAdultEntity.setId(4L);
        almostAdultEntity.setFirstName("Almost 18");
        almostAdultEntity.setEmail("almost18@example.com");
        almostAdultEntity.setUserRole(UserRole.ATHLETE);
        almostAdultEntity.setDateOfBirth(LocalDate.now().minusYears(18).plusDays(1));

        JoinTeamRequest request = new JoinTeamRequest("ABC12345", 4L);

        when(userRepository.findByIdAndUserRole(4L, UserRole.ATHLETE)).thenReturn(Optional.of(almostAdultEntity));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> athleteService.joinTeamViaInvite(request));

        assertEquals("Underage athletes cannot join via invite code. Please ask your coach to add you to the team.", exception.getMessage());
    }
}

