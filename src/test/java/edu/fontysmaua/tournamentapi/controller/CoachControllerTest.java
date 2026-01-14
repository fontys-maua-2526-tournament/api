package edu.fontysmaua.tournamentapi.controller;

import edu.fontysmaua.tournamentapi.domain.Team;
import edu.fontysmaua.tournamentapi.domain.Tournament;
import edu.fontysmaua.tournamentapi.domain.User;
import edu.fontysmaua.tournamentapi.domain.request.AddAthleteToTeamRequest;
import edu.fontysmaua.tournamentapi.domain.request.SaveCoachRequest;
import edu.fontysmaua.tournamentapi.domain.request.UpdateTeamRequest;
import edu.fontysmaua.tournamentapi.domain.response.*;
import edu.fontysmaua.tournamentapi.enums.UserRole;
import edu.fontysmaua.tournamentapi.service.CoachService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoachControllerTest {

    @Mock
    private CoachService coachService;

    @InjectMocks
    private CoachController coachController;

    private User coachUser;
    private Team team;

    @BeforeEach
    void setUp() {
        coachUser = new User();
        coachUser.setId(1L);
        coachUser.setFirstName("John");
        coachUser.setEmail("john@example.com");
        coachUser.setUserRole(UserRole.COACH);

        team = Team.builder()
                .id(1L)
                .name("Test Team")
                .inviteCode("ABC12345")
                .build();
    }

    // --- findAll() tests ---

    @Test
    void findAll_ShouldReturnOkWithCoaches() {
        GetAllCoachesResponse expectedResponse = new GetAllCoachesResponse(List.of(coachUser));
        when(coachService.findAll()).thenReturn(expectedResponse);

        ResponseEntity<GetAllCoachesResponse> response = coachController.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getCoaches().size());
        assertEquals(UserRole.COACH, response.getBody().getCoaches().getFirst().getUserRole());
    }

    // --- getTournamentsByUserId() tests ---

    @Test
    void getTournamentsByUserId_ShouldReturnOkWithTournaments() {
        Long userId = 1L;
        Tournament tournament = new Tournament();
        GetTournamentsByUserIdResponse expectedResponse = new GetTournamentsByUserIdResponse(List.of(tournament));
        when(coachService.findTournamentsByUserId(userId)).thenReturn(expectedResponse);

        ResponseEntity<GetTournamentsByUserIdResponse> response = coachController.getTournamentsByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    // --- create() tests ---

    @Test
    void create_ShouldReturnOkWithSavedCoach() {
        SaveCoachRequest request = new SaveCoachRequest(null, "John", "john@example.com");
        SavedCoachResponse expectedResponse = new SavedCoachResponse(coachUser);
        when(coachService.create(request)).thenReturn(expectedResponse);

        ResponseEntity<SavedCoachResponse> response = coachController.create(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(UserRole.COACH, response.getBody().getUser().getUserRole());
    }

    // --- update() tests ---

    @Test
    void update_ShouldReturnBadRequest_WhenIdMismatch() {
        Long pathId = 1L;
        SaveCoachRequest request = new SaveCoachRequest(2L, "John", "john@example.com");

        ResponseEntity<SavedCoachResponse> response = coachController.update(pathId, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(coachService, never()).update(any());
    }

    @Test
    void update_ShouldReturnOkWithUpdatedCoach_WhenIdMatches() {
        Long pathId = 1L;
        SaveCoachRequest request = new SaveCoachRequest(1L, "John Updated", "john@example.com");
        SavedCoachResponse expectedResponse = new SavedCoachResponse(coachUser);
        when(coachService.update(request)).thenReturn(expectedResponse);

        ResponseEntity<SavedCoachResponse> response = coachController.update(pathId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    // --- delete() tests ---

    @Test
    void delete_ShouldReturnOkWithDeletedId() {
        Long id = 1L;
        when(coachService.delete(id)).thenReturn(id);

        ResponseEntity<Long> response = coachController.delete(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(id, response.getBody());
    }

    // --- disbandTeam() tests ---

    @Test
    void disbandTeam_ShouldReturnOkWithSuccessMessage() {
        Long teamId = 1L;
        doNothing().when(coachService).disbandTeam(teamId);

        ResponseEntity<String> response = coachController.disbandTeam(teamId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Team disbanded successfully", response.getBody());
    }

    // --- registerTeam() tests ---

    @Test
    void registerTeam_ShouldReturnOkWithSuccessMessage() {
        Long teamId = 1L;
        Long tournamentId = 2L;
        doNothing().when(coachService).registerTeamInTournament(teamId, tournamentId);

        ResponseEntity<String> response = coachController.registerTeam(teamId, tournamentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Team registered successfully", response.getBody());
    }

    // --- withdrawTeam() tests ---

    @Test
    void withdrawTeam_ShouldReturnOkWithSuccessMessage() {
        Long teamId = 1L;
        Long tournamentId = 2L;
        doNothing().when(coachService).withdrawTeamFromTournament(teamId, tournamentId);

        ResponseEntity<String> response = coachController.withdrawTeam(teamId, tournamentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Team withdrawn successfully", response.getBody());
    }

    // --- addUnderageAthleteToTeam() tests ---

    @Test
    void addUnderageAthleteToTeam_ShouldReturnOk_WhenSuccessful() {
        Long coachId = 1L;
        AddAthleteToTeamRequest request = new AddAthleteToTeamRequest(1L, 2L);
        TeamMemberResponse expectedResponse = new TeamMemberResponse(team, "Underage athlete added successfully");
        when(coachService.addUnderageAthleteToTeam(request, coachId)).thenReturn(expectedResponse);

        ResponseEntity<TeamMemberResponse> response = coachController.addUnderageAthleteToTeam(coachId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Underage athlete added successfully", response.getBody().getMessage());
    }

    // --- updateTeam() tests ---

    @Test
    void updateTeam_ShouldReturnBadRequest_WhenIdMismatch() {
        Long coachId = 1L;
        Long teamId = 1L;
        UpdateTeamRequest request = new UpdateTeamRequest(2L, "New Name");

        ResponseEntity<SavedTeamResponse> response = coachController.updateTeam(coachId, teamId, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(coachService, never()).updateTeam(any(), any());
    }

    @Test
    void updateTeam_ShouldReturnOk_WhenSuccessful() {
        Long coachId = 1L;
        Long teamId = 1L;
        UpdateTeamRequest request = new UpdateTeamRequest(1L, "Updated Team Name");
        SavedTeamResponse expectedResponse = new SavedTeamResponse(team);
        when(coachService.updateTeam(request, coachId)).thenReturn(expectedResponse);

        ResponseEntity<SavedTeamResponse> response = coachController.updateTeam(coachId, teamId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
