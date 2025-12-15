package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.Match;
import edu.fontysmaua.tournamentapi.domain.Team;
import edu.fontysmaua.tournamentapi.domain.Tournament;
import edu.fontysmaua.tournamentapi.domain.request.CreateMatchRequest;
import edu.fontysmaua.tournamentapi.mapper.TournamentMapper;
import edu.fontysmaua.tournamentapi.persistence.TournamentRepository;
import edu.fontysmaua.tournamentapi.service.BracketService;
import edu.fontysmaua.tournamentapi.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BracketServiceImpl implements BracketService {
    TournamentRepository tournamentRepository;
    TournamentMapper tournamentMapper;
    MatchService matchService;

    public boolean createAutoBracket(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .map(tournamentMapper::entityToModel)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with id: " + tournamentId));

        if (!tournament.getMatches().isEmpty()) {
            throw new IllegalStateException("Matches have already been scheduled for this tournament");
        }
        
        List<Team> teams = tournament.getTeams();
        int teamCount = teams.size();
        if (teamCount < 2 || (teamCount & (teamCount - 1)) != 0) {
            throw new IllegalArgumentException("Number of teams must be a power of two and at least 2");
        }
        // Initial round matches
        for (int i = 0; i < teamCount; i += 2) {
            Team teamA = teams.get(i);
            Team teamB = teams.get(i + 1);
            matchService.create(new CreateMatchRequest(1, tournamentId, teamA.getId(), teamB.getId(), null, null, null, null));
        }
        
        // Knockout rounds matches
        int rounds = (int) (Math.log(teamCount) + 1); // +1 to include final round
        for (int i = 1; i < rounds; i++) {
            int currentRound = i;
            List<Match> matchesThisRound = matchService.findAll().getMatches().stream()
                    .filter(m -> m.getRound() == currentRound)
                    .toList();
            for (int j = 0; j < matchesThisRound.size(); j += 2) {
                Match match1 = matchesThisRound.get(j);
                Match match2 = matchesThisRound.get(j + 1);
                matchService.create(new CreateMatchRequest(currentRound, tournamentId, null, null, match1.getId(), match2.getId(), null, null));
            }
        }
        return true;
    }
}
