package com.example.speedtype;

public class WebSocketMessage {
    private String type;
    private Object payload;
    private String gameCode;
    private String username;

    public enum MessageType {
        GAME_UPDATE,
        PLAYER_PROGRESS,
        GAME_START,
        GAME_FINISH,
        PLAYER_JOIN,
        PLAYER_LEAVE,
        ERROR
    }

    public WebSocketMessage() {}

    public WebSocketMessage(String type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public WebSocketMessage(String type, Object payload, String gameCode) {
        this.type = type;
        this.payload = payload;
        this.gameCode = gameCode;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Object getPayload() { return payload; }
    public void setPayload(Object payload) { this.payload = payload; }

    public String getGameCode() { return gameCode; }
    public void setGameCode(String gameCode) { this.gameCode = gameCode; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
