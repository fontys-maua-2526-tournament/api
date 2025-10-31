package edu.fontysmaua.tournamentapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NameAlreadyExistsException extends ResponseStatusException {
    public NameAlreadyExistsException() {
        super(HttpStatus.BAD_REQUEST, "NAME_ALREADY_EXISTS");
    }
}
