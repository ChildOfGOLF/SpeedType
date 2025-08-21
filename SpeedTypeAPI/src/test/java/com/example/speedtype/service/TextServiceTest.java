package com.example.speedtype.service;

import com.example.speedtype.entity.Text;
import com.example.speedtype.repository.TextRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TextServiceTest {
    @Mock
    private TextRepository textRepository;
    @InjectMocks
    private TextService textService;

    @Test
    void getRandomTextByDifficulty_ReturnsText_WhenTextsExist() {
        String difficulty = "easy";
        List<Text> texts = Arrays.asList(new Text(), new Text());
        when(textRepository.findByDifficulty(difficulty)).thenReturn(texts);

        Text result = textService.getRandomTextByDifficulty(difficulty);
        assertNotNull(result);
        verify(textRepository).findByDifficulty(difficulty);
    }

    @Test
    void getRandomTextByDifficulty_ThrowsException_WhenNoTexts() {
        String difficulty = "hard";
        when(textRepository.findByDifficulty(difficulty)).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                textService.getRandomTextByDifficulty(difficulty));
        assertTrue(exception.getMessage().contains("No texts found for difficulty"));
    }

    @Test
    void getRandomTextByDifficultyAndLanguage_ReturnsText_WhenTextsExist() {
        String difficulty = "medium";
        String language = "en";
        List<Text> texts = Arrays.asList(new Text(), new Text());
        when(textRepository.findByDifficultyAndLanguage(difficulty, language)).thenReturn(texts);

        Text result = textService.getRandomTextByDifficultyAndLanguage(difficulty, language);
        assertNotNull(result);
        verify(textRepository).findByDifficultyAndLanguage(difficulty, language);
    }

    @Test
    void getRandomTextByDifficultyAndLanguage_ThrowsException_WhenNoTexts() {
        String difficulty = "medium";
        String language = "ru";
        when(textRepository.findByDifficultyAndLanguage(difficulty, language)).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                textService.getRandomTextByDifficultyAndLanguage(difficulty, language));
        assertTrue(exception.getMessage().contains("No texts found for difficulty"));
    }
}

