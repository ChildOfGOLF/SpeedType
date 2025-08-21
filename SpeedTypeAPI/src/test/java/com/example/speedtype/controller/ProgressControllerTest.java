package com.example.speedtype.controller;

import com.example.speedtype.controller.ProgressController.ProgressDataPoint;
import com.example.speedtype.controller.ProgressController.ProgressSummary;
import com.example.speedtype.entity.TypingResult;
import com.example.speedtype.entity.User;
import com.example.speedtype.repository.TypingResultRepository;
import com.example.speedtype.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgressControllerTest {
    @Mock
    private TypingResultRepository typingResultRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ProgressController progressController;

    @Test
    void getUserProgress_ReturnsList() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        TypingResult result = new TypingResult();
        result.setDate(LocalDateTime.now());
        result.setTypingSpeed(100);
        result.setErrors(2);
        result.setDifficulty("easy");
        result.setLanguage("en");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(typingResultRepository.findByUserIdOrderByDateAsc(userId)).thenReturn(Arrays.asList(result));

        List<ProgressDataPoint> points = progressController.getUserProgress(userId, null, null, null, null);
        assertEquals(1, points.size());
        assertEquals(100, points.get(0).getSpeed());
    }

    @Test
    void getUserProgress_ThrowsException_WhenUserNotFound() {
        Long userId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () ->
                progressController.getUserProgress(userId, null, null, null, null));
    }

    @Test
    void getUserProgressSummary_ReturnsSummary() {
        Long userId = 1L;
        TypingResult result = new TypingResult();
        result.setTypingSpeed(120);
        result.setErrors(1);
        when(typingResultRepository.findByUserIdOrderByDateAsc(userId)).thenReturn(Arrays.asList(result));
        ProgressSummary summary = progressController.getUserProgressSummary(userId);
        assertEquals(120.0, summary.getAverageSpeed());
        assertEquals(1.0, summary.getAverageErrors());
        assertEquals(120, summary.getMaxSpeed());
        assertEquals(1, summary.getTotalTests());
    }

    @Test
    void getUserProgressSummary_ReturnsZeroSummary_WhenNoResults() {
        Long userId = 1L;
        when(typingResultRepository.findByUserIdOrderByDateAsc(userId)).thenReturn(Collections.emptyList());
        ProgressSummary summary = progressController.getUserProgressSummary(userId);
        assertEquals(0, summary.getAverageSpeed());
        assertEquals(0, summary.getAverageErrors());
        assertEquals(0, summary.getMaxSpeed());
        assertEquals(0, summary.getTotalTests());
    }

    @Test
    void getUserProgressByDifficulty_ReturnsGroupedMap() {
        Long userId = 1L;
        TypingResult result = new TypingResult();
        result.setDate(LocalDateTime.now());
        result.setTypingSpeed(80);
        result.setErrors(3);
        result.setDifficulty("medium");
        result.setLanguage("ru");
        when(typingResultRepository.findByUserIdOrderByDateAsc(userId)).thenReturn(Arrays.asList(result));
        Map<String, List<ProgressDataPoint>> map = progressController.getUserProgressByDifficulty(userId);
        assertTrue(map.containsKey("medium"));
        assertEquals(1, map.get("medium").size());
    }
}
