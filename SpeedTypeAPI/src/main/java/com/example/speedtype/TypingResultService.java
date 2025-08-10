package com.example.speedtype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypingResultService {

    private final TypingResultRepository typingResultRepository;
    private final UserRepository userRepository;

    @Autowired
    public TypingResultService(TypingResultRepository typingResultRepository, UserRepository userRepository) {
        this.typingResultRepository = typingResultRepository;
        this.userRepository = userRepository;
    }

    public List<TypingResult> getAllResults() {
        return typingResultRepository.findAll();
    }

    public List<TypingResult> getResultsByUser(String username) {
        return typingResultRepository.findByUserUsernameOrderByDateDesc(username);
    }

    public TypingResult saveResult(String username, int typingSpeed, int errors, String difficulty) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        TypingResult result = new TypingResult(user, typingSpeed, errors, difficulty);
        return typingResultRepository.save(result);
    }

    public TypingResult saveResult(String username, int typingSpeed, int errors, String difficulty, String language) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        TypingResult result = new TypingResult(user, typingSpeed, errors, difficulty, language);
        return typingResultRepository.save(result);
    }

    public void deleteResult(Long id) {
        typingResultRepository.deleteById(id);
    }
}
