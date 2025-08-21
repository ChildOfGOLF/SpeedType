package com.example.speedtype.controller;

import com.example.speedtype.dto.GameStateDTO;
import com.example.speedtype.entity.Game;
import com.example.speedtype.entity.User;
import com.example.speedtype.service.GameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameControllerTest {
    @Mock
    private GameService gameService;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private GameController gameController;

    @Test
    void createGame_ReturnsGameState_WhenSuccess() {
        User user = new User();
        Game game = new Game();
        game.setGameCode("ABC123");
        GameStateDTO gameStateDTO = new GameStateDTO();
        when(authentication.getPrincipal()).thenReturn(user);
        when(gameService.createGame(user)).thenReturn(game);
        when(gameService.getGameState("ABC123")).thenReturn(gameStateDTO);

        ResponseEntity<?> response = gameController.createGame(authentication);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(gameStateDTO, response.getBody());
    }

    @Test
    void createGame_ReturnsBadRequest_WhenException() {
        User user = new User();
        when(authentication.getPrincipal()).thenReturn(user);
        when(gameService.createGame(user)).thenThrow(new RuntimeException("Ошибка создания игры"));

        ResponseEntity<?> response = gameController.createGame(authentication);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Ошибка создания игры", response.getBody());
    }

    @Test
    void joinGame_ReturnsGameState_WhenSuccess() {
        String gameCode = "ABC123";
        User user = new User();
        Game game = new Game();
        GameStateDTO gameStateDTO = new GameStateDTO();
        when(authentication.getPrincipal()).thenReturn(user);
        when(gameService.joinGame(gameCode, user)).thenReturn(game);
        when(gameService.getGameState(gameCode)).thenReturn(gameStateDTO);

        ResponseEntity<?> response = gameController.joinGame(gameCode, authentication);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(gameStateDTO, response.getBody());
    }

    @Test
    void joinGame_ReturnsBadRequest_WhenException() {
        String gameCode = "ABC123";
        User user = new User();
        when(authentication.getPrincipal()).thenReturn(user);
        when(gameService.joinGame(gameCode, user)).thenThrow(new RuntimeException("Ошибка входа в игру"));

        ResponseEntity<?> response = gameController.joinGame(gameCode, authentication);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Ошибка входа в игру", response.getBody());
    }

    @Test
    void getGameState_ReturnsGameState_WhenSuccess() {
        String gameCode = "ABC123";
        GameStateDTO gameStateDTO = new GameStateDTO();
        when(gameService.getGameState(gameCode)).thenReturn(gameStateDTO);

        ResponseEntity<?> response = gameController.getGameState(gameCode);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(gameStateDTO, response.getBody());
    }

    @Test
    void getGameState_ReturnsBadRequest_WhenException() {
        String gameCode = "ABC123";
        when(gameService.getGameState(gameCode)).thenThrow(new RuntimeException("Игра не найдена"));

        ResponseEntity<?> response = gameController.getGameState(gameCode);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Игра не найдена", response.getBody());
    }
}

