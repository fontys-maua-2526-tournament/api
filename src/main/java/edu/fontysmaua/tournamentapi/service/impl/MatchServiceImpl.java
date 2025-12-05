package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.request.SaveMatchRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllMatchesResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetMatchByIdResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetAllUpcomingMatchesResponse;
import edu.fontysmaua.tournamentapi.enums.Status;
import edu.fontysmaua.tournamentapi.domain.response.SavedMatchResponse;
import edu.fontysmaua.tournamentapi.mapper.MatchMapper;
import edu.fontysmaua.tournamentapi.persistence.MatchRepository;
import edu.fontysmaua.tournamentapi.persistence.TeamRepository;
import edu.fontysmaua.tournamentapi.persistence.TournamentRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.MatchEntity;
import edu.fontysmaua.tournamentapi.persistence.entity.TeamEntity;
import edu.fontysmaua.tournamentapi.persistence.entity.TournamentEntity;
import edu.fontysmaua.tournamentapi.service.MatchService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class MatchServiceImpl implements MatchService {
    private final MatchRepository matchRepository;
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final MatchMapper matchMapper;

    @Override
    public GetAllMatchesResponse findAll() {
        return new GetAllMatchesResponse(matchMapper.entitiesToModels(matchRepository.findAll()));
    }

    @Override
    public GetMatchByIdResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Match ID cannot be null.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("Match ID must be greater than 0.");
        }

        MatchEntity match = matchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Match not found"));

        return new GetMatchByIdResponse(matchMapper.entityToModel(match));
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

    @Override
    public SavedMatchResponse update(SaveMatchRequest request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("Match ID cannot be null");
        }
        if (!matchRepository.existsById(request.getId())) {
            throw new EntityNotFoundException("Match does not exist in the database");
        }
        TournamentEntity tournament = tournamentRepository.findById(request.getTournamentId())
                .orElseThrow(() -> new EntityNotFoundException("Tournament not found"));

        TeamEntity team1 = teamRepository.findById(request.getTeam1Id())
                .orElseThrow(() -> new EntityNotFoundException("Team 1 not found"));

        TeamEntity team2 = teamRepository.findById(request.getTeam2Id())
                .orElseThrow(() -> new EntityNotFoundException("Team 2 not found"));

        MatchEntity entity = MatchEntity.builder()
                .id(request.getId())
                .tournament(tournament)
                .round(request.getRound())
                .team1(team1)
                .team2(team2)
                .team1Score(request.getTeam1Score())
                .team2Score(request.getTeam2Score())
                .build();

        MatchEntity updated = matchRepository.save(entity);

        return new SavedMatchResponse(matchMapper.entityToModel(updated));
    }

    @Override 
    public Long cancelMatch(Long matchId) {
        var match = matchRepository.findById(matchId).orElseThrow(() -> new RuntimeException("Match not found"));

        if (match.getStatus() ==  Status.CANCELLED){
            throw new RuntimeException("Match is already cancelled!");
        }

        if(match.getStatus() == Status.COMPLETED) {
            throw new RuntimeException("Cannot cancel a match with scores already set");
        }

        match.setStatus(Status.CANCELLED);
        matchRepository.save(match);
        return matchId;
    }
}
