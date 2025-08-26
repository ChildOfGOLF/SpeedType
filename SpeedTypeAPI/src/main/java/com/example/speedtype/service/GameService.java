package com.example.speedtype.service;

import com.example.speedtype.dto.GameStateDTO;
import com.example.speedtype.dto.PlayerProgressDTO;
import com.example.speedtype.entity.Game;
import com.example.speedtype.entity.GameProgress;
import com.example.speedtype.entity.Text;
import com.example.speedtype.entity.User;
import com.example.speedtype.repository.GameProgressRepository;
import com.example.speedtype.repository.GameRepository;
import com.example.speedtype.repository.TextRepository;
import com.example.speedtype.websocket.WebSocketMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameProgressRepository gameProgressRepository;

    @Autowired
    private TextRepository textRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private GameCacheService gameCacheService;

    private final Random random = new Random();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public Game createGame(User creator) {
        Optional<Game> activeGame = gameRepository.findActiveGameByUser(creator);
        if (activeGame.isPresent()) {
            throw new RuntimeException("У вас уже есть активная игра");
        }

        String gameCode = generateGameCode();

        List<Text> texts = textRepository.findAll();
        if (texts.isEmpty()) {
            throw new RuntimeException("Нет доступных текстов для игры");
        }
        Text randomText = texts.get(random.nextInt(texts.size()));

        Game game = new Game(gameCode, randomText, creator);
        game = gameRepository.save(game);

        GameProgress progress1 = new GameProgress(game, creator);
        gameProgressRepository.save(progress1);

        GameStateDTO gameState = createGameStateDTO(game);
        gameCacheService.cacheGameState(game.getGameCode(), gameState);

        // Запуск задачи автозакрытия через 3 минуты
        final String code = game.getGameCode();
        scheduler.schedule(() -> cancelIfNoSecondPlayer(code), 3, TimeUnit.MINUTES);

        return game;
    }

    private void cancelIfNoSecondPlayer(String gameCode) {
        Game game = gameRepository.findByGameCode(gameCode).orElse(null);
        if (game != null && game.getStatus() == Game.GameStatus.WAITING_FOR_PLAYER) {
            game.setStatus(Game.GameStatus.CANCELLED);
            game.setFinishedAt(LocalDateTime.now());
            gameRepository.save(game);
            broadcastGameUpdate(game); // уведомление через WebSocket
        }
    }

    public Game joinGame(String gameCode, User player) {
        Game game = gameRepository.findByGameCode(gameCode)
            .orElseThrow(() -> new RuntimeException("Игра не найдена"));

        if (game.getStatus() != Game.GameStatus.WAITING_FOR_PLAYER) {
            throw new RuntimeException("Игра недоступна для присоединения");
        }

        if (game.getPlayer1().getId().equals(player.getId())) {
            throw new RuntimeException("Вы не можете присоединиться к собственной игре");
        }

        Optional<Game> activeGame = gameRepository.findActiveGameByUser(player);
        if (activeGame.isPresent()) {
            throw new RuntimeException("У вас уже есть активная игра");
        }

        game.setPlayer2(player);
        game.setStatus(Game.GameStatus.READY_TO_START);
        game = gameRepository.save(game);

        GameProgress progress2 = new GameProgress(game, player);
        gameProgressRepository.save(progress2);

        broadcastGameUpdate(game);

        return game;
    }

    public void startGame(String gameCode) {
        Game game = gameRepository.findByGameCode(gameCode)
            .orElseThrow(() -> new RuntimeException("Игра не найдена"));

        if (game.getStatus() != Game.GameStatus.READY_TO_START) {
            throw new RuntimeException("Игра не готова к запуску");
        }

        game.setStatus(Game.GameStatus.IN_PROGRESS);
        game.setStartedAt(LocalDateTime.now());
        gameRepository.save(game);

        broadcastGameUpdate(game);
    }

    public void updatePlayerProgress(String gameCode, User user, int currentPosition, double wpm, double accuracy) {
        Game game = gameRepository.findByGameCode(gameCode)
            .orElseThrow(() -> new RuntimeException("Игра не найдена"));

        if (game.getStatus() != Game.GameStatus.IN_PROGRESS) {
            return;
        }

        GameProgress progress = gameProgressRepository.findByGameAndUser(game, user)
            .orElseThrow(() -> new RuntimeException("Прогресс игрока не найден"));

        progress.setCurrentPosition(currentPosition);
        progress.setWpm(wpm);
        progress.setAccuracy(accuracy);
        progress.setLastUpdated(LocalDateTime.now());

        PlayerProgressDTO progressDTO = new PlayerProgressDTO(user, progress);
        gameCacheService.cachePlayerProgress(gameCode, user.getUsername(), progressDTO);

        gameCacheService.incrementGameMetric(gameCode, "progress_updates");

        boolean gameFinished = false;
        if (currentPosition >= game.getText().getContent().length() && !progress.getIsFinished()) {
            progress.setIsFinished(true);
            progress.setFinishTime(LocalDateTime.now());

            if (game.getWinner() == null) {
                game.setWinner(user);
                game.setStatus(Game.GameStatus.FINISHED);
                game.setFinishedAt(LocalDateTime.now());
                gameRepository.save(game);

                gameCacheService.removeGameFromCache(gameCode);
                gameFinished = true;
            }
        }

        if (gameFinished || currentPosition % 10 == 0) {
            gameProgressRepository.save(progress);
        }

        if (gameFinished) {
            broadcastGameUpdate(game);
        } else {
            broadcastProgressUpdate(game);
        }
    }

    public void updatePlayerProgressByUsername(String gameCode, String username, int currentPosition, double wpm, double accuracy) {
        Game game = gameRepository.findByGameCode(gameCode)
            .orElseThrow(() -> new RuntimeException("Игра не найдена"));

        User user = null;
        if (game.getPlayer1().getUsername().equals(username)) {
            user = game.getPlayer1();
        } else if (game.getPlayer2() != null && game.getPlayer2().getUsername().equals(username)) {
            user = game.getPlayer2();
        }

        if (user == null) {
            throw new RuntimeException("Пользователь не является участником игры");
        }

        updatePlayerProgress(gameCode, user, currentPosition, wpm, accuracy);
    }

    public GameStateDTO getGameState(String gameCode) {
        GameStateDTO cachedState = gameCacheService.getCachedGameState(gameCode);
        if (cachedState != null) {
            return cachedState;
        }

        Game game = gameRepository.findByGameCode(gameCode)
            .orElseThrow(() -> new RuntimeException("Игра не найдена"));

        GameStateDTO gameState = createGameStateDTO(game);

        if (game.getStatus() == Game.GameStatus.IN_PROGRESS) {
            gameCacheService.cacheGameState(gameCode, gameState);
        }

        return gameState;
    }

    public List<Game> getWaitingGames() {
        return gameRepository.findWaitingGames();
    }

    public Game getActiveGame(User user) {
        Optional<Game> activeGame = gameRepository.findActiveGameByUser(user);
        return activeGame.orElse(null);
    }

    public void cancelGame(String gameCode, User user) {
        Game game = gameRepository.findByGameCode(gameCode)
            .orElseThrow(() -> new RuntimeException("Игра не найдена"));

        if (!game.getPlayer1().getId().equals(user.getId()) &&
            (game.getPlayer2() == null || !game.getPlayer2().getId().equals(user.getId()))) {
            throw new RuntimeException("Вы не являетесь участником этой игры");
        }

        game.setStatus(Game.GameStatus.CANCELLED);
        game.setFinishedAt(LocalDateTime.now());
        gameRepository.save(game);

        broadcastGameUpdate(game);
    }

    private void broadcastGameUpdate(Game game) {
        GameStateDTO gameState = createGameStateDTO(game);
        messagingTemplate.convertAndSend("/topic/game/" + game.getGameCode(),
            new WebSocketMessage("GAME_UPDATE", gameState));
    }

    private void broadcastProgressUpdate(Game game) {
        GameStateDTO gameState = createGameStateDTO(game);
        messagingTemplate.convertAndSend("/topic/game/" + game.getGameCode(),
            new WebSocketMessage("PROGRESS_UPDATE", gameState));
    }

    private GameStateDTO createGameStateDTO(Game game) {
        GameStateDTO dto = new GameStateDTO(game);

        List<GameProgress> progresses = gameProgressRepository.findByGameOrderByLastUpdatedDesc(game);

        for (GameProgress progress : progresses) {
            PlayerProgressDTO playerProgress = new PlayerProgressDTO(progress.getUser(), progress);

            if (progress.getUser().getId().equals(game.getPlayer1().getId())) {
                dto.setPlayer1(playerProgress);
            } else if (game.getPlayer2() != null && progress.getUser().getId().equals(game.getPlayer2().getId())) {
                dto.setPlayer2(playerProgress);
            }
        }

        return dto;
    }

    private String generateGameCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }
}
