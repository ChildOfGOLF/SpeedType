package com.example.speedtype;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class GameCacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper redisObjectMapper;

    private static final String GAME_PREFIX = "game:";
    private static final String ACTIVE_GAMES_KEY = "active_games";
    private static final String PLAYER_PROGRESS_PREFIX = "progress:";

    private static final Duration GAME_STATE_TTL = Duration.ofHours(2); // 2 часа для состояния игры
    private static final Duration PROGRESS_TTL = Duration.ofMinutes(30); // 30 минут для прогресса
    private static final Duration ACTIVE_GAMES_TTL = Duration.ofMinutes(10); // 10 минут для списка активных игр

    public void cacheGameState(String gameCode, GameStateDTO gameState) {
        try {
            String key = GAME_PREFIX + gameCode;
            redisTemplate.opsForValue().set(key, gameState, GAME_STATE_TTL);

            redisTemplate.opsForSet().add(ACTIVE_GAMES_KEY, gameCode);
            redisTemplate.expire(ACTIVE_GAMES_KEY, ACTIVE_GAMES_TTL);

        } catch (Exception e) {
            System.err.println("Error caching game state: " + e.getMessage());
        }
    }

    public GameStateDTO getCachedGameState(String gameCode) {
        try {
            String key = GAME_PREFIX + gameCode;
            Object cached = redisTemplate.opsForValue().get(key);

            if (cached instanceof GameStateDTO) {
                return (GameStateDTO) cached;
            }

            return null;
        } catch (Exception e) {
            System.err.println("Error getting cached game state: " + e.getMessage());
            return null;
        }
    }

    public void cachePlayerProgress(String gameCode, String username, PlayerProgressDTO progress) {
        try {
            String key = PLAYER_PROGRESS_PREFIX + gameCode + ":" + username;
            redisTemplate.opsForValue().set(key, progress, PROGRESS_TTL);

            String countKey = "progress_updates:" + gameCode + ":" + username;
            redisTemplate.opsForValue().increment(countKey);
            redisTemplate.expire(countKey, PROGRESS_TTL);

        } catch (Exception e) {
            System.err.println("Error caching player progress: " + e.getMessage());
        }
    }

    public PlayerProgressDTO getCachedPlayerProgress(String gameCode, String username) {
        try {
            String key = PLAYER_PROGRESS_PREFIX + gameCode + ":" + username;
            Object cached = redisTemplate.opsForValue().get(key);

            if (cached instanceof PlayerProgressDTO) {
                return (PlayerProgressDTO) cached;
            }

            return null;
        } catch (Exception e) {
            System.err.println("Error getting cached player progress: " + e.getMessage());
            return null;
        }
    }

    public Set<Object> getActiveGames() {
        try {
            return redisTemplate.opsForSet().members(ACTIVE_GAMES_KEY);
        } catch (Exception e) {
            System.err.println("Error getting active games: " + e.getMessage());
            return Set.of();
        }
    }

    public void removeGameFromCache(String gameCode) {
        try {
            redisTemplate.delete(GAME_PREFIX + gameCode);

            redisTemplate.opsForSet().remove(ACTIVE_GAMES_KEY, gameCode);

            Set<String> keys = redisTemplate.keys(PLAYER_PROGRESS_PREFIX + gameCode + ":*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }

            Set<String> countKeys = redisTemplate.keys("progress_updates:" + gameCode + ":*");
            if (countKeys != null && !countKeys.isEmpty()) {
                redisTemplate.delete(countKeys);
            }

        } catch (Exception e) {
            System.err.println("Error removing game from cache: " + e.getMessage());
        }
    }

    public boolean isGameActive(String gameCode) {
        try {
            return redisTemplate.opsForSet().isMember(ACTIVE_GAMES_KEY, gameCode);
        } catch (Exception e) {
            System.err.println("Error checking if game is active: " + e.getMessage());
            return false;
        }
    }

    public void incrementGameMetric(String gameCode, String metric) {
        try {
            String key = "game_metrics:" + gameCode + ":" + metric;
            redisTemplate.opsForValue().increment(key);
            redisTemplate.expire(key, GAME_STATE_TTL);
        } catch (Exception e) {
            System.err.println("Error incrementing game metric: " + e.getMessage());
        }
    }

    public Long getGameMetric(String gameCode, String metric) {
        try {
            String key = "game_metrics:" + gameCode + ":" + metric;
            Object value = redisTemplate.opsForValue().get(key);
            return value instanceof Number ? ((Number) value).longValue() : 0L;
        } catch (Exception e) {
            System.err.println("Error getting game metric: " + e.getMessage());
            return 0L;
        }
    }
}
