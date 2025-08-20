package com.example.speedtype.service;

import com.example.speedtype.entity.TypingResult;
import com.example.speedtype.entity.User;
import com.example.speedtype.dto.TypingResultResponseDTO;
import com.example.speedtype.repository.TypingResultRepository;
import com.example.speedtype.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypingResultService {

    private final TypingResultRepository typingResultRepository;
    private final UserRepository userRepository;
    private final LeaderboardService leaderboardService;

    @Autowired
    public TypingResultService(TypingResultRepository typingResultRepository, UserRepository userRepository, LeaderboardService leaderboardService) {
        this.typingResultRepository = typingResultRepository;
        this.userRepository = userRepository;
        this.leaderboardService = leaderboardService;
    }

    public List<TypingResult> getAllResults() {
        return typingResultRepository.findAll();
    }

    @Cacheable(value = "userResults", key = "#username")
    public List<TypingResultResponseDTO> getResultsByUserDTO(String username) {
        List<TypingResult> results = typingResultRepository.findByUserUsernameOrderByDateDesc(username);
        return results.stream()
                .map(TypingResultResponseDTO::new)
                .collect(java.util.stream.Collectors.toList());
    }

    // старый метод для внутреннего использования без кэширования
    public List<TypingResult> getResultsByUser(String username) {
        return typingResultRepository.findByUserUsernameOrderByDateDesc(username);
    }

    @CacheEvict(value = "userResults", key = "#username")
    public TypingResult saveResult(String username, int typingSpeed, int errors, String difficulty) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        TypingResult result = new TypingResult(user, typingSpeed, errors, difficulty);
        TypingResult savedResult = typingResultRepository.save(result);

        leaderboardService.clearLeaderboardCache();

        return savedResult;
    }

    @CacheEvict(value = "userResults", key = "#username")
    public TypingResult saveResult(String username, int typingSpeed, int errors, String difficulty, String language) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        TypingResult result = new TypingResult(user, typingSpeed, errors, difficulty, language);
        TypingResult savedResult = typingResultRepository.save(result);

        leaderboardService.clearLeaderboardCache();

        return savedResult;
    }

    public void deleteResult(Long id) {
        typingResultRepository.deleteById(id);

        leaderboardService.clearLeaderboardCache();
    }
}
