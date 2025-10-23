package edu.fontysmaua.tournamentapi.domain.Team.response;

public class CreateTeamResponse {
    private String id;
    private boolean success;
    private String message;

    public CreateTeamResponse(String id, boolean success, String message) {
        this.id = id;
        this.success = success;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
