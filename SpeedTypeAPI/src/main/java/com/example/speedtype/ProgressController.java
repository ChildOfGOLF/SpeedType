package com.example.speedtype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class ProgressController {

    @Autowired
    private TypingResultRepository typingResultRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user/{userId}")
    public List<ProgressDataPoint> getUserProgress(@PathVariable Long userId,
                                                  @RequestParam(required = false) String difficulty,
                                                  @RequestParam(required = false) String language,
                                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<TypingResult> results = typingResultRepository.findByUserIdOrderByDateAsc(userId);

        return results.stream()
                .filter(result -> difficulty == null || result.getDifficulty().equals(difficulty))
                .filter(result -> language == null || result.getLanguage().equals(language))
                .filter(result -> startDate == null || result.getDate().isAfter(startDate))
                .filter(result -> endDate == null || result.getDate().isBefore(endDate))
                .map(result -> new ProgressDataPoint(
                        result.getDate(),
                        result.getTypingSpeed(),
                        result.getErrors(),
                        result.getDifficulty(),
                        result.getLanguage()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/user/{userId}/summary")
    public ProgressSummary getUserProgressSummary(@PathVariable Long userId) {
        List<TypingResult> results = typingResultRepository.findByUserIdOrderByDateAsc(userId);

        if (results.isEmpty()) {
            return new ProgressSummary(0, 0, 0, 0, 0);
        }

        double avgSpeed = results.stream().mapToInt(TypingResult::getTypingSpeed).average().orElse(0);
        double avgErrors = results.stream().mapToInt(TypingResult::getErrors).average().orElse(0);
        int maxSpeed = results.stream().mapToInt(TypingResult::getTypingSpeed).max().orElse(0);
        int totalTests = results.size();

        int improvementPercent = 0;
        if (results.size() >= 10) {
            double firstAvg = results.subList(0, Math.min(10, results.size()))
                    .stream().mapToInt(TypingResult::getTypingSpeed).average().orElse(0);
            double lastAvg = results.subList(Math.max(0, results.size() - 10), results.size())
                    .stream().mapToInt(TypingResult::getTypingSpeed).average().orElse(0);

            if (firstAvg > 0) {
                improvementPercent = (int) ((lastAvg - firstAvg) / firstAvg * 100);
            }
        }

        return new ProgressSummary(avgSpeed, avgErrors, maxSpeed, totalTests, improvementPercent);
    }

    @GetMapping("/user/{userId}/by-difficulty")
    public Map<String, List<ProgressDataPoint>> getUserProgressByDifficulty(@PathVariable Long userId) {
        List<TypingResult> results = typingResultRepository.findByUserIdOrderByDateAsc(userId);

        return results.stream()
                .map(result -> new ProgressDataPoint(
                        result.getDate(),
                        result.getTypingSpeed(),
                        result.getErrors(),
                        result.getDifficulty(),
                        result.getLanguage()
                ))
                .collect(Collectors.groupingBy(ProgressDataPoint::getDifficulty));
    }

    public static class ProgressDataPoint {
        private LocalDateTime date;
        private int speed;
        private int errors;
        private String difficulty;
        private String language;

        public ProgressDataPoint(LocalDateTime date, int speed, int errors, String difficulty, String language) {
            this.date = date;
            this.speed = speed;
            this.errors = errors;
            this.difficulty = difficulty;
            this.language = language;
        }

        public LocalDateTime getDate() { return date; }
        public int getSpeed() { return speed; }
        public int getErrors() { return errors; }
        public String getDifficulty() { return difficulty; }
        public String getLanguage() { return language; }
    }

    public static class ProgressSummary {
        private double averageSpeed;
        private double averageErrors;
        private int maxSpeed;
        private int totalTests;
        private int improvementPercent;

        public ProgressSummary(double averageSpeed, double averageErrors, int maxSpeed, int totalTests, int improvementPercent) {
            this.averageSpeed = averageSpeed;
            this.averageErrors = averageErrors;
            this.maxSpeed = maxSpeed;
            this.totalTests = totalTests;
            this.improvementPercent = improvementPercent;
        }

        public double getAverageSpeed() { return averageSpeed; }
        public double getAverageErrors() { return averageErrors; }
        public int getMaxSpeed() { return maxSpeed; }
        public int getTotalTests() { return totalTests; }
        public int getImprovementPercent() { return improvementPercent; }
    }
}
