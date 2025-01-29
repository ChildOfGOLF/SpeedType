package com.example.speedtype;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/texts")
public class TextController {

    @Autowired
    private TextRepository textRepository;

    // Получить тексты по сложности
    @GetMapping
    public List<Text> getTextsByDifficulty(@RequestParam String difficulty) {
        return textRepository.findByDifficulty(difficulty);
    }
}
