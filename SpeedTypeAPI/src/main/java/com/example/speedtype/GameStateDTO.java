package com.example.speedtype;

public class GameStateDTO {
    private String gameCode;
    private String status;
    private String textContent;
    private PlayerProgressDTO player1;
    private PlayerProgressDTO player2;
    private String winnerUsername;
    private long gameStartTime;
    private boolean gameStarted;

    public GameStateDTO() {}

    public GameStateDTO(Game game) {
        this.gameCode = game.getGameCode();
        this.status = game.getStatus().toString();
        this.textContent = game.getText() != null ? game.getText().getContent() : "";
        this.gameStarted = game.getStatus() == Game.GameStatus.IN_PROGRESS;
        this.gameStartTime = game.getStartedAt() != null ?
            java.time.ZoneOffset.UTC.getRules().getOffset(game.getStartedAt()).getTotalSeconds() * 1000 : 0;

        if (game.getWinner() != null) {
            this.winnerUsername = game.getWinner().getUsername();
        }
    }

    public String getGameCode() { return gameCode; }
    public void setGameCode(String gameCode) { this.gameCode = gameCode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTextContent() { return textContent; }
    public void setTextContent(String textContent) { this.textContent = textContent; }

    public PlayerProgressDTO getPlayer1() { return player1; }
    public void setPlayer1(PlayerProgressDTO player1) { this.player1 = player1; }

    public PlayerProgressDTO getPlayer2() { return player2; }
    public void setPlayer2(PlayerProgressDTO player2) { this.player2 = player2; }

    public String getWinnerUsername() { return winnerUsername; }
    public void setWinnerUsername(String winnerUsername) { this.winnerUsername = winnerUsername; }

    public long getGameStartTime() { return gameStartTime; }
    public void setGameStartTime(long gameStartTime) { this.gameStartTime = gameStartTime; }

    public boolean isGameStarted() { return gameStarted; }
    public void setGameStarted(boolean gameStarted) { this.gameStarted = gameStarted; }
}
