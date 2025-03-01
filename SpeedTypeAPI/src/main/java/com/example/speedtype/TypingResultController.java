package com.example.speedtype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/results")
public class TypingResultController {

    private final TypingResultService typingResultService;

    @Autowired
    public TypingResultController(TypingResultService typingResultService) {
        this.typingResultService = typingResultService;
    }

    @GetMapping
    public List<TypingResult> getAllResults() {
        return typingResultService.getAllResults();
    }

    @GetMapping("/{id}")
    public TypingResult getResultById(@PathVariable Long id) {
        return typingResultService.getResultById(id).orElse(null);
    }

    @PostMapping
    public TypingResult saveResult(@RequestBody TypingResult result) {
        return typingResultService.saveResult(result);
    }
}
