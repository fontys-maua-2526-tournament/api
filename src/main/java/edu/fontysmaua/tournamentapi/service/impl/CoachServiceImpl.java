package edu.fontysmaua.tournamentapi.service.impl;

import edu.fontysmaua.tournamentapi.domain.request.AddAthleteToTeamRequest;
import edu.fontysmaua.tournamentapi.domain.request.SaveCoachRequest;
import edu.fontysmaua.tournamentapi.domain.request.UpdateTeamRequest;
import edu.fontysmaua.tournamentapi.domain.response.GetAllCoachesResponse;
import edu.fontysmaua.tournamentapi.domain.response.GetTournamentsByUserIdResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedCoachResponse;
import edu.fontysmaua.tournamentapi.domain.response.SavedTeamResponse;
import edu.fontysmaua.tournamentapi.domain.response.TeamMemberResponse;
import edu.fontysmaua.tournamentapi.enums.UserRole;
import edu.fontysmaua.tournamentapi.exception.NameAlreadyExistsException;
import edu.fontysmaua.tournamentapi.mapper.TeamMapper;
import edu.fontysmaua.tournamentapi.mapper.TournamentMapper;
import edu.fontysmaua.tournamentapi.mapper.UserMapper;
import edu.fontysmaua.tournamentapi.persistence.UserRepository;
import edu.fontysmaua.tournamentapi.persistence.TeamRepository;
import edu.fontysmaua.tournamentapi.persistence.TournamentRepository;
import edu.fontysmaua.tournamentapi.persistence.entity.TeamEntity;
import edu.fontysmaua.tournamentapi.persistence.entity.UserEntity;
import edu.fontysmaua.tournamentapi.service.CoachService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CoachServiceImpl implements CoachService {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TournamentRepository tournamentRepository;
    private final UserMapper userMapper;
    private final TeamMapper teamMapper;
    private final TournamentMapper tournamentMapper;

    @Override
    public GetAllCoachesResponse findAll() {
        var coaches = userMapper.entitiesToModels(userRepository.findAllByUserRole(UserRole.COACH));
        return new GetAllCoachesResponse(coaches);
    }

    @Override
    public GetTournamentsByUserIdResponse findTournamentsByUserId(Long userId) {
        userRepository.findByIdAndUserRole(userId, UserRole.COACH)
                .orElseThrow(() -> new IllegalArgumentException("Coach not found"));
        
        return new GetTournamentsByUserIdResponse(tournamentMapper.entitiesToModels(tournamentRepository.findAllByTeamsUsersId(userId)));
    }

    @Override
    public SavedCoachResponse create(SaveCoachRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new NameAlreadyExistsException("Email already exists");
        }

        var userEntity = new UserEntity();
        userEntity.setFirstName(request.getName());
        userEntity.setEmail(request.getEmail());
        userEntity.setUserRole(UserRole.COACH);

        var saved = userRepository.save(userEntity);
        return new SavedCoachResponse(userMapper.entityToModel(saved));
    }

    @Override
    public SavedCoachResponse update(SaveCoachRequest request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("Id is required");
        }

        var existing = userRepository.findByIdAndUserRole(request.getId(), UserRole.COACH)
                .orElseThrow(() -> new IllegalArgumentException("Coach not found"));

        if (!existing.getEmail().equals(request.getEmail()) 
                && userRepository.existsByEmailAndIdNot(request.getEmail(), request.getId())) {
            throw new NameAlreadyExistsException("Email already exists");
        }

        existing.setFirstName(request.getName());
        existing.setEmail(request.getEmail());

        var updated = userRepository.save(existing);
        return new SavedCoachResponse(userMapper.entityToModel(updated));
    }

    @Override
    public Long delete(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid coach id");
        }
        
        userRepository.findByIdAndUserRole(id, UserRole.COACH)
                .orElseThrow(() -> new IllegalArgumentException("Coach not found"));
        
        userRepository.deleteById(id);
        return id;
    }

    @Override
    public void disbandTeam(Long teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new IllegalArgumentException("Team does not exist");
        }
        teamRepository.deleteById(teamId);
    }

    @Override
    public void registerTeamInTournament(Long teamId, Long tournamentId) {
        if (!teamRepository.existsById(teamId)) {
            throw new IllegalArgumentException("Team not found");
        }
        if (!tournamentRepository.existsById(tournamentId)) {
            throw new IllegalArgumentException("Tournament not found");
        }
        System.out.printf("Team %d registered in tournament %d%n", teamId, tournamentId);
    }

    @Override
    public void withdrawTeamFromTournament(Long teamId, Long tournamentId) {
        if (!teamRepository.existsById(teamId)) {
            throw new IllegalArgumentException("Team not found");
        }
        if (!tournamentRepository.existsById(tournamentId)) {
            throw new IllegalArgumentException("Tournament not found");
        }
        System.out.printf("Team %d withdrawn from tournament %d%n", teamId, tournamentId);
    }

    @Override
    @Transactional
    public TeamMemberResponse addUnderageAthleteToTeam(AddAthleteToTeamRequest request, Long coachId) {
        // Verify the coach exists
        userRepository.findByIdAndUserRole(coachId, UserRole.COACH)
                .orElseThrow(() -> new IllegalArgumentException("Coach not found"));

        // Find the team and verify the coach owns it
        TeamEntity team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        if (team.getCoach() == null || !team.getCoach().getId().equals(coachId)) {
            throw new IllegalArgumentException("You are not the coach of this team");
        }

        // Find the athlete
        UserEntity athlete = userRepository.findByIdAndUserRole(request.getAthleteId(), UserRole.ATHLETE)
                .orElseThrow(() -> new IllegalArgumentException("Athlete not found"));

        // Verify the athlete is underage
        if (!athlete.isUnderage()) {
            throw new IllegalArgumentException("Athlete is not underage. Adult athletes must join via invite code.");
        }

        // Check if athlete is already in the team
        if (team.getMembers().contains(athlete)) {
            throw new IllegalArgumentException("Athlete is already a member of this team");
        }

        // Add athlete to team
        team.getMembers().add(athlete);
        TeamEntity savedTeam = teamRepository.save(team);

        return new TeamMemberResponse(teamMapper.entityToModel(savedTeam), "Underage athlete added successfully");
    }

    @Override
    @Transactional
    public SavedTeamResponse updateTeam(UpdateTeamRequest request, Long coachId) {
        // Verify the coach exists
        userRepository.findByIdAndUserRole(coachId, UserRole.COACH)
                .orElseThrow(() -> new IllegalArgumentException("Coach not found"));

        // Find the team
        TeamEntity team = teamRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        // Verify the coach owns the team
        if (team.getCoach() == null || !team.getCoach().getId().equals(coachId)) {
            throw new IllegalArgumentException("You are not the coach of this team");
        }

        // Update team name
        team.setName(request.getName());

        TeamEntity savedTeam = teamRepository.save(team);
        return new SavedTeamResponse(teamMapper.entityToModel(savedTeam));
    }
}
