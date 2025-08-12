package com.example.speedtype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
                    text.setLanguage(updatedText.getLanguage());
                    return textRepository.save(text);
                }).orElseThrow(() -> new RuntimeException("Text not found"));
    }

    @DeleteMapping("/{id}")
    public void deleteText(@PathVariable Long id) {
        textRepository.deleteById(id);
    }

    @PostMapping("/bulk-upload")
    public List<Text> bulkUploadTexts(@RequestParam("file") MultipartFile file,
                                     @RequestParam("difficulty") String difficulty,
                                     @RequestParam("language") String language) {
        List<Text> savedTexts = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"))) {
            String line;
            StringBuilder currentText = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    if (currentText.length() > 0) {
                        Text text = new Text(currentText.toString().trim(), difficulty, language);
                        savedTexts.add(textRepository.save(text));
                        currentText.setLength(0);
                    }
                    continue;
                }

                if (line.matches("^-{3,}$") || line.matches("^={3,}$")) {
                    if (currentText.length() > 0) {
                        Text text = new Text(currentText.toString().trim(), difficulty, language);
                        savedTexts.add(textRepository.save(text));
                        currentText.setLength(0);
                    }
                    continue;
                }

                if (currentText.length() > 0) {
                    currentText.append(" ");
                }
                currentText.append(line);
            }

            if (currentText.length() > 0) {
                Text text = new Text(currentText.toString().trim(), difficulty, language);
                savedTexts.add(textRepository.save(text));
            }

        } catch (Exception e) {
            throw new RuntimeException("Error processing file: " + e.getMessage());
        }

        return savedTexts;
    }
}
