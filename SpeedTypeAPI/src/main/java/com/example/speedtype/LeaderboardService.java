package com.example.speedtype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {

    private final TypingResultRepository typingResultRepository;

    @Autowired
    public LeaderboardService(TypingResultRepository typingResultRepository) {
        this.typingResultRepository = typingResultRepository;
    }

    @Cacheable(value = "leaderboard", key = "'top_users_' + #limit")
    public List<LeaderboardEntryDTO> getTopUsersByWPM(int limit) {
        List<Object[]> results = typingResultRepository.findTopUsersByWPM(limit);
        return results.stream()
                .map(this::mapToLeaderboardEntry)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "leaderboard", key = "'top_users_difficulty_' + #difficulty + '_' + #limit")
    public List<LeaderboardEntryDTO> getTopUsersByDifficultyAndWPM(String difficulty, int limit) {
        List<Object[]> results = typingResultRepository.findTopUsersByDifficultyAndWPM(difficulty, limit);
        return results.stream()
                .map(this::mapToLeaderboardEntry)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "leaderboard", key = "'top_users_language_' + #language + '_' + #limit")
    public List<LeaderboardEntryDTO> getTopUsersByLanguageAndWPM(String language, int limit) {
        List<Object[]> results = typingResultRepository.findTopUsersByLanguageAndWPM(language, limit);
        return results.stream()
                .map(this::mapToLeaderboardEntry)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "leaderboard", allEntries = true)
    public void clearLeaderboardCache() {
    }

    private LeaderboardEntryDTO mapToLeaderboardEntry(Object[] result) {
        LeaderboardEntryDTO entry = new LeaderboardEntryDTO();
        entry.setUsername((String) result[0]);
        entry.setBestWPM(((Number) result[1]).intValue());
        entry.setAverageWPM(((Number) result[2]).intValue());
        entry.setTotalTests(((Number) result[3]).intValue());
        entry.setDifficulty((String) result[4]);
        entry.setLanguage((String) result[5]);

        Object dateObject = result[6];
        if (dateObject instanceof java.sql.Timestamp) {
            entry.setLastTestDate(((java.sql.Timestamp) dateObject).toLocalDateTime());
        } else if (dateObject instanceof java.time.LocalDateTime) {
            entry.setLastTestDate((java.time.LocalDateTime) dateObject);
        }

        return entry;
    }
}
