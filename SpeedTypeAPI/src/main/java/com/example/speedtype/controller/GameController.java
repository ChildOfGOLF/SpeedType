package com.example.speedtype.controller;

import com.example.speedtype.entity.Game;
import com.example.speedtype.service.GameService;
import com.example.speedtype.dto.GameStateDTO;
import com.example.speedtype.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "*")
public class GameController {

    @Autowired
    private GameService gameService;

    @PostMapping("/create")
    public ResponseEntity<?> createGame(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Game game = gameService.createGame(user);
            GameStateDTO gameState = gameService.getGameState(game.getGameCode());
            return ResponseEntity.ok(gameState);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{gameCode}/join")
    public ResponseEntity<?> joinGame(@PathVariable String gameCode, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Game game = gameService.joinGame(gameCode, user);
            GameStateDTO gameState = gameService.getGameState(gameCode);
            return ResponseEntity.ok(gameState);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{gameCode}")
    public ResponseEntity<?> getGameState(@PathVariable String gameCode) {
        try {
            GameStateDTO gameState = gameService.getGameState(gameCode);
            return ResponseEntity.ok(gameState);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/waiting")
    public ResponseEntity<List<Game>> getWaitingGames() {
        List<Game> waitingGames = gameService.getWaitingGames();
        return ResponseEntity.ok(waitingGames);
    }

    @PostMapping("/{gameCode}/start")
    public ResponseEntity<?> startGame(@PathVariable String gameCode) {
        try {
            gameService.startGame(gameCode);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/my-active")
    public ResponseEntity<?> getMyActiveGame(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Game activeGame = gameService.getActiveGame(user);
            if (activeGame != null) {
                GameStateDTO gameState = gameService.getGameState(activeGame.getGameCode());
                return ResponseEntity.ok(gameState);
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{gameCode}/cancel")
    public ResponseEntity<?> cancelGame(@PathVariable String gameCode, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            gameService.cancelGame(gameCode, user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
