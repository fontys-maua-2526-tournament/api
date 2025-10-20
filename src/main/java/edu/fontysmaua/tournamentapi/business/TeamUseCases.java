package edu.fontysmaua.tournamentapi.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.fontysmaua.tournamentapi.persistence.TeamRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.TeamEntity;

@Service
public class TeamUseCases {

    //create - save in the database
    @Autowired
    private TeamRepository teamRepository;
    
    public void save(TeamEntity team) {
        this.teamRepository.save(team);
    }

}
