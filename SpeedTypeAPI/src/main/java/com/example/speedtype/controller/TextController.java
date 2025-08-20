package com.example.speedtype.controller;
import com.example.speedtype.entity.Text;
import com.example.speedtype.service.TextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/texts")
public class TextController {

    @Autowired
    private TextService textService;

    @GetMapping
    public ResponseEntity<Text> getText(
            @RequestParam String difficulty,
            @RequestParam(defaultValue = "en") String language) {
        try {
            return ResponseEntity.ok(textService.getRandomTextByDifficultyAndLanguage(difficulty, language));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
