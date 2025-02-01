package com.example.speedtype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/texts")
@CrossOrigin(origins = "*")
public class AdminTextController {

    @Autowired
    private TextRepository textRepository;

    @GetMapping
    public List<Text> getAllTexts() {
        return textRepository.findAll();
    }

    @PostMapping
    public Text addText(@RequestBody Text text) {
        return textRepository.save(text);
    }

    @PutMapping("/{id}")
    public Text updateText(@PathVariable Long id, @RequestBody Text updatedText) {
        return textRepository.findById(id)
                .map(text -> {
                    text.setContent(updatedText.getContent());
                    text.setDifficulty(updatedText.getDifficulty());
                    return textRepository.save(text);
                }).orElseThrow(() -> new RuntimeException("Text not found"));
    }

    @DeleteMapping("/{id}")
    public void deleteText(@PathVariable Long id) {
        textRepository.deleteById(id);
    }
}
