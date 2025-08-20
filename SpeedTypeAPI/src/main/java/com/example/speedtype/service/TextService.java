package com.example.speedtype.service;

import com.example.speedtype.entity.Text;
import com.example.speedtype.repository.TextRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class TextService {

    @Autowired
    private TextRepository textRepository;

    public Text getRandomTextByDifficulty(String difficulty) {
        List<Text> texts = textRepository.findByDifficulty(difficulty);
        if (texts.isEmpty()) {
            throw new RuntimeException("No texts found for difficulty: " + difficulty);
        }
        return texts.get(new Random().nextInt(texts.size()));
    }

    public Text getRandomTextByDifficultyAndLanguage(String difficulty, String language) {
        List<Text> texts = textRepository.findByDifficultyAndLanguage(difficulty, language);
        if (texts.isEmpty()) {
            throw new RuntimeException("No texts found for difficulty: " + difficulty + " and language: " + language);
        }
        return texts.get(new Random().nextInt(texts.size()));
    }
}
