package com.example.speedtype.websocket;

import com.example.speedtype.service.GameService;
import com.example.speedtype.dto.PlayerProgressUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GameWebSocketController {

    @Autowired
    private GameService gameService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/game/{gameCode}/progress")
    public void updateProgress(@DestinationVariable String gameCode,
                              @Payload PlayerProgressUpdateDTO update,
                              java.security.Principal principal) {
        try {
            String username = update.getUsername();

            if (username == null || username.trim().isEmpty()) {
                System.err.println("Username is null or empty for game " + gameCode);
                messagingTemplate.convertAndSend("/topic/game/" + gameCode,
                    new WebSocketMessage("ERROR", "Username is required", gameCode));
                return;
            }

            System.out.println("Updating progress for user: " + username + " in game: " + gameCode);
            System.out.println("Position: " + update.getCurrentPosition() + ", WPM: " + update.getWpm() + ", Accuracy: " + update.getAccuracy());

            gameService.updatePlayerProgressByUsername(gameCode, username,
                update.getCurrentPosition(), update.getWpm(), update.getAccuracy());

        } catch (Exception e) {
            System.err.println("Error updating progress: " + e.getMessage());
            e.printStackTrace();
            messagingTemplate.convertAndSend("/topic/game/" + gameCode,
                new WebSocketMessage("ERROR", e.getMessage(), gameCode));
        }
    }

    @MessageMapping("/game/{gameCode}/join")
    public void joinGame(@DestinationVariable String gameCode) {
        try {
            messagingTemplate.convertAndSend("/topic/game/" + gameCode,
                new WebSocketMessage("PLAYER_JOIN", "Player joined", gameCode));
        } catch (Exception e) {
            messagingTemplate.convertAndSend("/topic/game/" + gameCode,
                new WebSocketMessage("ERROR", e.getMessage(), gameCode));
        }
    }

    @MessageMapping("/game/{gameCode}/start")
    public void startGame(@DestinationVariable String gameCode) {
        try {
            messagingTemplate.convertAndSend("/topic/game/" + gameCode,
                new WebSocketMessage("GAME_START", "Game started", gameCode));
        } catch (Exception e) {
            messagingTemplate.convertAndSend("/topic/game/" + gameCode,
                new WebSocketMessage("ERROR", e.getMessage(), gameCode));
        }
    }
}
