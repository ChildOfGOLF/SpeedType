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
        System.out.println("TypingResultService: Looking for results for user: " + username);
        List<TypingResult> results = typingResultRepository.findByUserUsernameOrderByDateDesc(username);
        System.out.println("TypingResultService: Found " + results.size() + " results");
        return results;
    }

    public TypingResult saveResult(String username, int typingSpeed, int errors, String difficulty) {
        System.out.println("TypingResultService: Saving result for user: " + username);

        // Найти пользователя по имени
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        System.out.println("TypingResultService: Found user with ID: " + user.getId());

        // Создать результат с правильной связью
        TypingResult result = new TypingResult(user, typingSpeed, errors, difficulty);
        TypingResult savedResult = typingResultRepository.save(result);

        System.out.println("TypingResultService: Saved result with ID: " + savedResult.getId());
        return savedResult;
    }

    public void deleteResult(Long id) {
        typingResultRepository.deleteById(id);
    }
}
