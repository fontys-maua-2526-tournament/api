package edu.fontysmaua.tournamentapi.business.impl.Team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.fontysmaua.tournamentapi.business.TeamUseCases;
import edu.fontysmaua.tournamentapi.domain.Team.DeleteTeamResponse;
import edu.fontysmaua.tournamentapi.persistence.TeamRepository;

@Service
public class DeleteTeamImpl implements TeamUseCases.DeleteTeam {

    @Autowired
    private TeamRepository teamRepository;

    @Override
    public DeleteTeamResponse deleteTeamById(String teamId) {
        Long id = Long.parseLong(teamId); // conversão de String → Long

        boolean exists = teamRepository.existsById(id);

        if (!exists) {
            return new DeleteTeamResponse(
                teamId,
                false,
                "Time com ID " + teamId + " não encontrado."
            );
        }

        teamRepository.deleteById(id);

        return new DeleteTeamResponse(
            teamId,
            true,
            "Time com ID " + teamId + " foi deletado com sucesso."
        );
    }
}
