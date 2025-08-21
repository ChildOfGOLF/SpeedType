package com.example.speedtype.service;

import com.example.speedtype.dto.LeaderboardEntryDTO;
import com.example.speedtype.repository.TypingResultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceTest {
    @Mock
    private TypingResultRepository typingResultRepository;
    @InjectMocks
    private LeaderboardService leaderboardService;

    @Test
    void getTopUsers_ReturnsList() {
        Object[] row = {
            "user1",
            100,
            90,
            5,
            "easy",
            "en",
            LocalDateTime.now()
        };
        when(typingResultRepository.findTopUsersByWPM(5)).thenReturn(Collections.singletonList(row));
        List<LeaderboardEntryDTO> result = leaderboardService.getTopUsers(5);
        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals(100, result.get(0).getBestWPM());
        assertEquals(90, result.get(0).getAverageWPM());
        assertEquals(5, result.get(0).getTotalTests());
        assertEquals("easy", result.get(0).getDifficulty());
        assertEquals("en", result.get(0).getLanguage());
        assertNotNull(result.get(0).getLastTestDate());
    }

    @Test
    void getTopUsersByDifficulty_ReturnsList() {
        Object[] row = {
            "user2", 120, 110, 7, "easy", "en", LocalDateTime.now()
        };
        when(typingResultRepository.findTopUsersByDifficultyAndWPM("easy", 3)).thenReturn(Collections.singletonList(row));
        List<LeaderboardEntryDTO> result = leaderboardService.getTopUsersByDifficulty("easy", 3);
        assertEquals(1, result.size());
        assertEquals("user2", result.get(0).getUsername());
        assertEquals(120, result.get(0).getBestWPM());
        assertEquals(110, result.get(0).getAverageWPM());
        assertEquals(7, result.get(0).getTotalTests());
        assertEquals("easy", result.get(0).getDifficulty());
        assertEquals("en", result.get(0).getLanguage());
        assertNotNull(result.get(0).getLastTestDate());
    }

    @Test
    void getTopUsersByLanguage_ReturnsList() {
        Object[] row = {
            "user3", 90, 80, 3, "medium", "en", LocalDateTime.now()
        };
        when(typingResultRepository.findTopUsersByLanguageAndWPM("en", 2)).thenReturn(Collections.singletonList(row));
        List<LeaderboardEntryDTO> result = leaderboardService.getTopUsersByLanguage("en", 2);
        assertEquals(1, result.size());
        assertEquals("user3", result.get(0).getUsername());
        assertEquals(90, result.get(0).getBestWPM());
        assertEquals(80, result.get(0).getAverageWPM());
        assertEquals(3, result.get(0).getTotalTests());
        assertEquals("medium", result.get(0).getDifficulty());
        assertEquals("en", result.get(0).getLanguage());
        assertNotNull(result.get(0).getLastTestDate());
    }

    @Test
    void getTopUsers_ReturnsEmptyList_WhenNoResults() {
        when(typingResultRepository.findTopUsersByWPM(5)).thenReturn(Collections.emptyList());
        List<LeaderboardEntryDTO> result = leaderboardService.getTopUsers(5);
        assertTrue(result.isEmpty());
    }
}
