package edu.fontysmaua.tournamentapi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.fontysmaua.tournamentapi.persistence.TeamRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.TeamEntity;

@Service
public class CreateTeamImpl implements TeamUseCases.CreateTeam {

    @Autowired
    private TeamRepository teamRepository;

    @Override
    public CreateTeamResponse createTeam(TeamEntity team) {
        try {
            TeamEntity savedTeam = teamRepository.save(team);

            return new CreateTeamResponse(
                savedTeam.getId().toString(),
                true,
                "Time criado com sucesso com ID " + savedTeam.getId()
            );

        } catch (Exception e) {
            return new CreateTeamResponse(
                null,
                false,
                "Erro ao criar o time: " + e.getMessage()
            );
        }
    }
}
