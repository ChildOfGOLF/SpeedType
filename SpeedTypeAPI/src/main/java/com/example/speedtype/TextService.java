package com.example.speedtype;

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
}
