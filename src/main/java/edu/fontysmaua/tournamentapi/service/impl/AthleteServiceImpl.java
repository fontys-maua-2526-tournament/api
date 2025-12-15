package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.request.JoinTeamRequest;
import edu.fontysmaua.tournamentapi.domain.response.TeamMemberResponse;
import edu.fontysmaua.tournamentapi.enums.UserRole;
import edu.fontysmaua.tournamentapi.mapper.TeamMapper;
import edu.fontysmaua.tournamentapi.persistence.TeamRepository;
import edu.fontysmaua.tournamentapi.persistence.UserRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.TeamEntity;
import edu.fontysmaua.tournamentapi.persistence.entity.UserEntity;
import edu.fontysmaua.tournamentapi.service.AthleteService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AthleteServiceImpl implements AthleteService {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    @Override
    @Transactional
    public TeamMemberResponse joinTeamViaInvite(JoinTeamRequest request) {
        // Find the athlete
        UserEntity athlete = userRepository.findByIdAndUserRole(request.getAthleteId(), UserRole.ATHLETE)
                .orElseThrow(() -> new IllegalArgumentException("Athlete not found"));

        // Verify the athlete is NOT underage (adults use invite code)
        if (athlete.isUnderage()) {
            throw new IllegalArgumentException("Underage athletes cannot join via invite code. Please ask your coach to add you to the team.");
        }

        // Find the team by invite code
        TeamEntity team = teamRepository.findByInviteCode(request.getInviteCode())
                .orElseThrow(() -> new IllegalArgumentException("Invalid invite code"));

        // Check if athlete is already in the team
        if (team.getMembers().contains(athlete)) {
            throw new IllegalArgumentException("You are already a member of this team");
        }

        // Add athlete to team
        team.getMembers().add(athlete);
        TeamEntity savedTeam = teamRepository.save(team);

        return new TeamMemberResponse(teamMapper.entityToModel(savedTeam), "Successfully joined the team");
    }
}

