package com.example.speedtype.service;

import com.example.speedtype.entity.Game;
import com.example.speedtype.entity.User;
import com.example.speedtype.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {
    @Mock
    private GameRepository gameRepository;
    @Mock
    private com.example.speedtype.repository.TextRepository textRepository;
    @Mock
    private com.example.speedtype.repository.GameProgressRepository gameProgressRepository;
    @Mock
    private com.example.speedtype.service.GameCacheService gameCacheService;
    @InjectMocks
    private GameService gameService;

    @Test
    void createGame_ReturnsGame_WhenNoActiveGame() {
        User user = new User();
        when(gameRepository.findActiveGameByUser(user)).thenReturn(Optional.empty());
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));
        com.example.speedtype.entity.Text text = new com.example.speedtype.entity.Text();
        text.setId(1L);
        text.setContent("Sample text");
        when(textRepository.findAll()).thenReturn(java.util.List.of(text));
        when(gameProgressRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(gameCacheService).cacheGameState(anyString(), any());

        Game game = gameService.createGame(user);
        assertNotNull(game);
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void createGame_ThrowsException_WhenActiveGameExists() {
        User user = new User();
        Game activeGame = new Game();
        when(gameRepository.findActiveGameByUser(user)).thenReturn(Optional.of(activeGame));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                gameService.createGame(user));
        assertEquals("У вас уже есть активная игра", exception.getMessage());
    }
}
