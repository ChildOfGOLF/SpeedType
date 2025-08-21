package com.example.speedtype.controller;

import com.example.speedtype.dto.LeaderboardEntryDTO;
import com.example.speedtype.service.LeaderboardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaderboardControllerTest {
    @Mock
    private LeaderboardService leaderboardService;
    @InjectMocks
    private LeaderboardController leaderboardController;

    @Test
    void getTopUsers_ReturnsList() {
        LeaderboardEntryDTO entry = new LeaderboardEntryDTO();
        entry.setUsername("user1");
        entry.setBestWPM(100);
        when(leaderboardService.getTopUsers(10)).thenReturn(Arrays.asList(entry));
        List<LeaderboardEntryDTO> result = leaderboardController.getTopUsers(10);
        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getUsername());
    }

    @Test
    void getTopUsersByDifficulty_ReturnsList() {
        LeaderboardEntryDTO entry = new LeaderboardEntryDTO();
        entry.setUsername("user2");
        entry.setBestWPM(120);
        when(leaderboardService.getTopUsersByDifficulty("easy", 5)).thenReturn(Arrays.asList(entry));
        List<LeaderboardEntryDTO> result = leaderboardController.getTopUsersByDifficulty("easy", 5);
        assertEquals(1, result.size());
        assertEquals("user2", result.get(0).getUsername());
    }

    @Test
    void getTopUsersByLanguage_ReturnsList() {
        LeaderboardEntryDTO entry = new LeaderboardEntryDTO();
        entry.setUsername("user3");
        entry.setBestWPM(90);
        when(leaderboardService.getTopUsersByLanguage("en", 3)).thenReturn(Arrays.asList(entry));
        List<LeaderboardEntryDTO> result = leaderboardController.getTopUsersByLanguage("en", 3);
        assertEquals(1, result.size());
        assertEquals("user3", result.get(0).getUsername());
    }

    @Test
    void clearCache_ReturnsOk() {
        ResponseEntity<String> response = leaderboardController.clearCache();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Кэш лидерборда успешно очищен", response.getBody());
        verify(leaderboardService).clearLeaderboardCache();
    }
}

