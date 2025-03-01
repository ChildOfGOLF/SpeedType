package com.example.speedtype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TypingResultService {

    private final TypingResultRepository typingResultRepository;

    @Autowired
    public TypingResultService(TypingResultRepository typingResultRepository) {
        this.typingResultRepository = typingResultRepository;
    }

    public TypingResult saveResult(TypingResult result) {
        return typingResultRepository.save(result);
    }

    public List<TypingResult> getAllResults() {
        return typingResultRepository.findAll();
    }

    public Optional<TypingResult> getResultById(Long id) {
        return typingResultRepository.findById(id);
    }
}
