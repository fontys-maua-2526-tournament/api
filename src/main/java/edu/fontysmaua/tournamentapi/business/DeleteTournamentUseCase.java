package edu.fontysmaua.tournamentapi.business;

import edu.fontysmaua.tournamentapi.domain.Tournament;

import java.util.Optional;

public interface DeleteTournamentUseCase {
    Long delete(Long id);
}
