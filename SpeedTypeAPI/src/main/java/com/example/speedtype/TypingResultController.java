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

    @GetMapping("/all")
    public List<TypingResultResponseDTO> getAllResults() {
        return typingResultService.getAllResults().stream()
                .map(TypingResultResponseDTO::new)
                .collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/my")
    public ResponseEntity<List<TypingResultResponseDTO>> getMyResults(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authentication.getName();

        try {
            List<TypingResultResponseDTO> responseResults = typingResultService.getResultsByUserDTO(username);
            return ResponseEntity.ok(responseResults);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<TypingResultResponseDTO> saveResult(
            @RequestBody TypingResultDTO resultDTO,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authentication.getName();

        try {
            TypingResult savedResult = typingResultService.saveResult(
                    username,
                    resultDTO.getTypingSpeed(),
                    resultDTO.getErrors(),
                    resultDTO.getDifficulty(),
                    resultDTO.getLanguage() != null ? resultDTO.getLanguage() : "en"
            );

            return ResponseEntity.ok(new TypingResultResponseDTO(savedResult));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}