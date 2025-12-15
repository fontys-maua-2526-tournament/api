package edu.fontysmaua.tournamentapi.controller;

import edu.fontysmaua.tournamentapi.domain.Tournament;
import edu.fontysmaua.tournamentapi.domain.User;
import edu.fontysmaua.tournamentapi.domain.request.SaveCoachRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllCoachesResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetTournamentsByUserIdResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedCoachResponse;
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
    private SaveCoachRequest saveCoachRequest;
    private SavedCoachResponse savedCoachResponse;

    @BeforeEach
    void setUp() {
        coachUser = new User();
        coachUser.setId(1L);
        coachUser.setFirstName("John");
        coachUser.setEmail("john@example.com");
        coachUser.setUserRole(UserRole.COACH);

        saveCoachRequest = new SaveCoachRequest(null, "John", "john@example.com");
        savedCoachResponse = new SavedCoachResponse(coachUser);
    }

    // --- findAll() tests ---

    @Test
    void findAll_ShouldReturnOkWithCoaches() {
        // Arrange
        GetAllCoachesResponse expectedResponse = new GetAllCoachesResponse(List.of(coachUser));
        when(coachService.findAll()).thenReturn(expectedResponse);

        // Act
        ResponseEntity<GetAllCoachesResponse> response = coachController.findAll();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getCoaches().size());
        assertEquals(UserRole.COACH, response.getBody().getCoaches().getFirst().getUserRole());
        
        verify(coachService, times(1)).findAll();
    }

    @Test
    void findAll_ShouldReturnOkWithEmptyList_WhenNoCoachesExist() {
        // Arrange
        GetAllCoachesResponse expectedResponse = new GetAllCoachesResponse(List.of());
        when(coachService.findAll()).thenReturn(expectedResponse);

        // Act
        ResponseEntity<GetAllCoachesResponse> response = coachController.findAll();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getCoaches().isEmpty());
        
        verify(coachService, times(1)).findAll();
    }

    // --- getTournamentsByUserId() tests ---

    @Test
    void getTournamentsByUserId_ShouldReturnOkWithTournaments() {
        // Arrange
        Long userId = 1L;
        Tournament tournament = new Tournament();
        GetTournamentsByUserIdResponse expectedResponse = new GetTournamentsByUserIdResponse(List.of(tournament));
        when(coachService.findTournamentsByUserId(userId)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<GetTournamentsByUserIdResponse> response = coachController.getTournamentsByUserId(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTournaments().size());
        
        verify(coachService, times(1)).findTournamentsByUserId(userId);
    }

    // --- create() tests ---

    @Test
    void create_ShouldReturnOkWithSavedCoach() {
        // Arrange
        when(coachService.create(saveCoachRequest)).thenReturn(savedCoachResponse);

        // Act
        ResponseEntity<SavedCoachResponse> response = coachController.create(saveCoachRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(coachUser, response.getBody().getUser());
        assertEquals(UserRole.COACH, response.getBody().getUser().getUserRole());
        
        verify(coachService, times(1)).create(saveCoachRequest);
    }

    @Test
    void create_ShouldPassRequestToService() {
        // Arrange
        SaveCoachRequest request = new SaveCoachRequest(null, "New Coach", "newcoach@example.com");
        User newCoach = new User();
        newCoach.setId(2L);
        newCoach.setFirstName("New Coach");
        newCoach.setEmail("newcoach@example.com");
        newCoach.setUserRole(UserRole.COACH);
        
        SavedCoachResponse expectedResponse = new SavedCoachResponse(newCoach);
        when(coachService.create(request)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<SavedCoachResponse> response = coachController.create(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(UserRole.COACH, response.getBody().getUser().getUserRole());
        
        verify(coachService, times(1)).create(request);
    }

    // --- update() tests ---

    @Test
    void update_ShouldReturnBadRequest_WhenIdMismatch() {
        // Arrange
        Long pathId = 1L;
        SaveCoachRequest request = new SaveCoachRequest(2L, "John", "john@example.com");

        // Act
        ResponseEntity<SavedCoachResponse> response = coachController.update(pathId, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        
        verify(coachService, never()).update(any());
    }

    @Test
    void update_ShouldReturnOkWithUpdatedCoach_WhenIdMatches() {
        // Arrange
        Long pathId = 1L;
        SaveCoachRequest request = new SaveCoachRequest(1L, "John Updated", "john@example.com");
        
        User updatedCoach = new User();
        updatedCoach.setId(1L);
        updatedCoach.setFirstName("John Updated");
        updatedCoach.setEmail("john@example.com");
        updatedCoach.setUserRole(UserRole.COACH);
        
        SavedCoachResponse expectedResponse = new SavedCoachResponse(updatedCoach);
        when(coachService.update(request)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<SavedCoachResponse> response = coachController.update(pathId, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John Updated", response.getBody().getUser().getFirstName());
        assertEquals(UserRole.COACH, response.getBody().getUser().getUserRole());
        
        verify(coachService, times(1)).update(request);
    }

    // --- delete() tests ---

    @Test
    void delete_ShouldReturnOkWithDeletedId() {
        // Arrange
        Long id = 1L;
        when(coachService.delete(id)).thenReturn(id);

        // Act
        ResponseEntity<Long> response = coachController.delete(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(id, response.getBody());
        
        verify(coachService, times(1)).delete(id);
    }

    // --- disbandTeam() tests ---

    @Test
    void disbandTeam_ShouldReturnOkWithSuccessMessage() {
        // Arrange
        Long teamId = 1L;
        doNothing().when(coachService).disbandTeam(teamId);

        // Act
        ResponseEntity<String> response = coachController.disbandTeam(teamId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Team disbanded successfully", response.getBody());
        
        verify(coachService, times(1)).disbandTeam(teamId);
    }

    // --- registerTeam() tests ---

    @Test
    void registerTeam_ShouldReturnOkWithSuccessMessage() {
        // Arrange
        Long teamId = 1L;
        Long tournamentId = 2L;
        doNothing().when(coachService).registerTeamInTournament(teamId, tournamentId);

        // Act
        ResponseEntity<String> response = coachController.registerTeam(teamId, tournamentId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Team registered successfully", response.getBody());
        
        verify(coachService, times(1)).registerTeamInTournament(teamId, tournamentId);
    }

    // --- withdrawTeam() tests ---

    @Test
    void withdrawTeam_ShouldReturnOkWithSuccessMessage() {
        // Arrange
        Long teamId = 1L;
        Long tournamentId = 2L;
        doNothing().when(coachService).withdrawTeamFromTournament(teamId, tournamentId);

        // Act
        ResponseEntity<String> response = coachController.withdrawTeam(teamId, tournamentId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Team withdrawn successfully", response.getBody());
        
        verify(coachService, times(1)).withdrawTeamFromTournament(teamId, tournamentId);
    }
}

