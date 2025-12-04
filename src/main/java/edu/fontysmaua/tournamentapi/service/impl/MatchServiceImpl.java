package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.response.GetAllMatchesResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetAllUpcomingMatchesResponse;
import edu.fontysmaua.tournamentapi.enums.Status;
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
