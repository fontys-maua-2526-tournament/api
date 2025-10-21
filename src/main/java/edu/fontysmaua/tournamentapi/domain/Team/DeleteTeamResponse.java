package edu.fontysmaua.tournamentapi.domain.Team;

public class DeleteTeamResponse {
  private String id;
  private boolean deleted;
  private String message;

  public DeleteTeamResponse(String id, boolean deleted, String message) {
    this.id = id;
    this.deleted = deleted;
    this.message = message;
  }

  public String getId() {
    return id;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public String getMessage() {
    return message;
  }
}
