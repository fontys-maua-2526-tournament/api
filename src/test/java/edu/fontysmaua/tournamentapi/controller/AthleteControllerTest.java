package edu.fontysmaua.tournamentapi.controller;

import edu.fontysmaua.tournamentapi.domain.Team;
import edu.fontysmaua.tournamentapi.domain.request.JoinTeamRequest;
import edu.fontysmaua.tournamentapi.domain.response.TeamMemberResponse;
import edu.fontysmaua.tournamentapi.service.AthleteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AthleteControllerTest {

    @Mock
    private AthleteService athleteService;

    @InjectMocks
    private AthleteController athleteController;

    private Team team;

    @BeforeEach
    void setUp() {
        team = Team.builder()
                .id(1L)
                .name("Test Team")
                .inviteCode("ABC12345")
                .build();
    }

    @Test
    void joinTeamViaInvite_ShouldReturnOk_WhenSuccessful() {
        JoinTeamRequest request = new JoinTeamRequest("ABC12345", 1L);
        TeamMemberResponse expectedResponse = new TeamMemberResponse(team, "Successfully joined the team");
        when(athleteService.joinTeamViaInvite(request)).thenReturn(expectedResponse);

        ResponseEntity<TeamMemberResponse> response = athleteController.joinTeamViaInvite(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Successfully joined the team", response.getBody().getMessage());
        assertEquals(team, response.getBody().getTeam());
        
        verify(athleteService, times(1)).joinTeamViaInvite(request);
    }

    @Test
    void joinTeamViaInvite_ShouldCallServiceWithCorrectRequest() {
        JoinTeamRequest request = new JoinTeamRequest("XYZ98765", 5L);
        TeamMemberResponse expectedResponse = new TeamMemberResponse(team, "Successfully joined the team");
        when(athleteService.joinTeamViaInvite(request)).thenReturn(expectedResponse);

        athleteController.joinTeamViaInvite(request);

        verify(athleteService).joinTeamViaInvite(argThat(r -> 
            r.getInviteCode().equals("XYZ98765") && r.getAthleteId().equals(5L)
        ));
    }
}

