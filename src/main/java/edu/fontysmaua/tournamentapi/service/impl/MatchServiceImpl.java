package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.response.GetAllMatchesResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetAllUpcomingMatchesResponse;
import edu.fontysmaua.tournamentapi.mapper.MatchMapper;
import edu.fontysmaua.tournamentapi.persistence.MatchRepository;
import edu.fontysmaua.tournamentapi.service.MatchService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class MatchServiceImpl implements MatchService {
    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;

    @Override
    public GetAllMatchesResponse findAll() {
        return new GetAllMatchesResponse(matchMapper.entitiesToModels(matchRepository.findAll()));
    }

    @Override
    public GetAllUpcomingMatchesResponse findAllUpcoming() {
        return new GetAllUpcomingMatchesResponse(matchMapper.entitiesToModels(matchRepository.findAllByTournamentStartTime(LocalDateTime.now())));
    }

    @Override
    public SavedMatchResponse create(SaveMatchRequest request) {
        TournamentEntity tournament = tournamentRepository.findById(request.getTournamentId())
            .orElseThrow(() -> new RuntimeException("Tournament not found"));

        TeamEntity team1 = teamRepository.findById(request.getTeam1Id())
            .orElseThrow(() -> new RuntimeException("Team 1 not found"));

        TeamEntity team2 = teamRepository.findById(request.getTeam2Id())
            .orElseThrow(() -> new RuntimeException("Team 2 not found"));

        MatchEntity savedMatch = matchRepository.save(
            MatchEntity.builder()
                .tournament(tournament)
                .round(request.getRound())
                .team1(team1)
                .team2(team2)
                .team1Score(request.getTeam1Score())
                .team2Score(request.getTeam2Score())
                .build()
        );
        return new SavedMatchResponse(matchMapper.entityToModel(savedMatch));
    }
}
