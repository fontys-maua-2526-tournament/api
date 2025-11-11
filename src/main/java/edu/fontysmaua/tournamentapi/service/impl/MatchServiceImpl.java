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
}
