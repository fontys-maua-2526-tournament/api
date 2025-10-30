package edu.fontysmaua.tournamentapi.controller;

import edu.fontysmaua.tournamentapi.domain.Tournament;
import edu.fontysmaua.tournamentapi.domain.request.SaveTournamentRequest;
import edu.fontysmaua.tournamentapi.domain.response.SavedTournamentResponse;
import edu.fontysmaua.tournamentapi.service.TournamentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class TournamentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TournamentService tournamentService;

    @Test
    void createTournament_shouldReturn200ResponseWithTournamentId() throws Exception {
        // Arrange
        SaveTournamentRequest request = SaveTournamentRequest.builder()
                .name("Tournament")
                .address("Fontys Rachelsmolen")
                .startTime(LocalDateTime.of(LocalDate.of(2026,1,29), LocalTime.of(9, 0)))
                .endTime(LocalDateTime.of(LocalDate.of(2026,1,30), LocalTime.of(9, 0)))
                .build();

        Tournament tournament = new Tournament();
        tournament.setId(1L);
        SavedTournamentResponse response = new SavedTournamentResponse(tournament);

        when(tournamentService.create(request))
                .thenReturn(response);

        // Act
        mockMvc.perform(post("/tournaments")
                .contentType(APPLICATION_JSON_VALUE)
                .content("""
                        {
                            "name": "Tournament",
                            "address": "Fontys Rachelsmolen",
                            "startTime": "2026-01-29T09:00:00",
                            "endTime": "2026-01-30T09:00:00"
                        }
                        """))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", APPLICATION_JSON_VALUE))
                .andExpect(content().json("""
                  {"tournament": {"id": 1}}
                """));

        // Assert
        verify(tournamentService, times(1)).create(request);
    }

    @Test
    void createTournament_WhenInputIsInvalid_ShouldReturn400Response() throws Exception {
        // Act
        mockMvc.perform(post("/tournaments")
                        .contentType(APPLICATION_JSON_VALUE)
                        .content("""
                        {
                            "name": "",
                            "address": "Fontys Rachelsmolen",
                            "startTime": "2026-01-29T09:00:00",
                            "endTime": "2026-01-30T09:00:00"
                        }
                        """))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Assert
        verify(tournamentService, never()).create(any(SaveTournamentRequest.class));
    }
}