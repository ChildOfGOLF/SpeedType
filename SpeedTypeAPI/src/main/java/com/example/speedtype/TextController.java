package com.example.speedtype;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/texts")
public class TextController {

    @Autowired
    private TextRepository textRepository;
    
    @GetMapping
    public List<Text> getTextsByDifficulty(@RequestParam String difficulty) {
        return textRepository.findByDifficulty(difficulty);
    }
    @PutMapping("/texts/{id}")
    public ResponseEntity<Text> updateText(@PathVariable Long id, @RequestBody Text updatedText) {
        return textRepository.findById(id)
                .map(text -> {
                    text.setContent(updatedText.getContent());
                    text.setDifficulty(updatedText.getDifficulty());
                    textRepository.save(text);
                    return ResponseEntity.ok(text);
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
