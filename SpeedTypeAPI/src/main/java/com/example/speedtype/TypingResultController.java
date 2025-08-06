package com.example.speedtype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/results")
@CrossOrigin(origins = "*")
public class TypingResultController {

    private final TypingResultService typingResultService;

    @Autowired
    public TypingResultController(TypingResultService typingResultService) {
        this.typingResultService = typingResultService;
    }

    // Получить все результаты (только для админов)
    @GetMapping("/all")
    public List<TypingResultResponseDTO> getAllResults() {
        return typingResultService.getAllResults().stream()
                .map(TypingResultResponseDTO::new)
                .collect(java.util.stream.Collectors.toList());
    }

    // Получить результаты текущего пользователя
    @GetMapping("/my")
    public ResponseEntity<List<TypingResultResponseDTO>> getMyResults(Authentication authentication) {
        System.out.println("Getting results for user: " + (authentication != null ? authentication.getName() : "null"));

        if (authentication == null) {
            System.out.println("Authentication is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authentication.getName();
        System.out.println("Username from auth: " + username);

        try {
            List<TypingResult> results = typingResultService.getResultsByUser(username);
            System.out.println("Found " + results.size() + " results for user " + username);
            
            // Конвертируем в DTO для избежания циклических ссылок
            List<TypingResultResponseDTO> responseResults = results.stream()
                    .map(TypingResultResponseDTO::new)
                    .collect(java.util.stream.Collectors.toList());
            
            return ResponseEntity.ok(responseResults);
        } catch (Exception e) {
            System.err.println("Error getting results: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Сохранить новый результат
    @PostMapping
    public ResponseEntity<TypingResultResponseDTO> saveResult(@RequestBody TypingResultDTO resultDTO, Authentication authentication) {
        System.out.println("Saving result for user: " + (authentication != null ? authentication.getName() : "null"));

        if (authentication == null) {
            System.out.println("Authentication is null when saving");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authentication.getName();
        System.out.println("Saving result - Username: " + username + ", Speed: " + resultDTO.getTypingSpeed() + ", Errors: " + resultDTO.getErrors());

        try {
            // Используем новый метод сервиса, который принимает username
            TypingResult savedResult = typingResultService.saveResult(
                username,
                resultDTO.getTypingSpeed(),
                resultDTO.getErrors(),
                resultDTO.getDifficulty()
            );

            System.out.println("Result saved with ID: " + savedResult.getId());

            // Возвращаем DTO вместо прямого объекта
            return ResponseEntity.ok(new TypingResultResponseDTO(savedResult));
        } catch (Exception e) {
            System.err.println("Error saving result: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ...existing code...
}
