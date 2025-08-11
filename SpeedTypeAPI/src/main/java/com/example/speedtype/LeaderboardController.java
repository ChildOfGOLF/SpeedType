package com.example.speedtype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leaderboard")
@CrossOrigin(origins = "*")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @Autowired
    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping("/top")
    public List<LeaderboardEntryDTO> getTopUsers(@RequestParam(defaultValue = "10") int limit) {
        return leaderboardService.getTopUsersByWPM(limit);
    }

    @GetMapping("/top/difficulty/{difficulty}")
    public List<LeaderboardEntryDTO> getTopUsersByDifficulty(
            @PathVariable String difficulty,
            @RequestParam(defaultValue = "10") int limit) {
        return leaderboardService.getTopUsersByDifficultyAndWPM(difficulty, limit);
    }

    @GetMapping("/top/language/{language}")
    public List<LeaderboardEntryDTO> getTopUsersByLanguage(
            @PathVariable String language,
            @RequestParam(defaultValue = "10") int limit) {
        return leaderboardService.getTopUsersByLanguageAndWPM(language, limit);
    }

    @DeleteMapping("/cache")
    public ResponseEntity<String> clearCache() {
        leaderboardService.clearLeaderboardCache();
        return ResponseEntity.ok("Кэш лидерборда успешно очищен");
    }
}
