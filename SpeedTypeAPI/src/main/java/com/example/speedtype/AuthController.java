package com.example.speedtype;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        System.out.println("Registration attempt for user: " + userDTO.getUsername());

        if (userDTO.getUsername() == null || userDTO.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Username cannot be null or empty");
        }

        try {
            String token = userService.registerUser(userDTO.getUsername(), userDTO.getPassword());
            System.out.println("User registered successfully: " + userDTO.getUsername());

            return ResponseEntity.ok(new TokenResponse(token));
        } catch (RuntimeException e) {
            System.err.println("Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        System.out.println("Login attempt for user: " + userDTO.getUsername());

        try {
            String token = userService.authenticateUser(userDTO.getUsername(), userDTO.getPassword());
            System.out.println("Login successful for user: " + userDTO.getUsername());

            return ResponseEntity.ok(new TokenResponse(token));
        } catch (RuntimeException e) {
            System.err.println("Login failed: " + e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Missing or invalid Authorization header");
            }
            String token = authHeader.substring(7);
            User user = userService.getUserFromToken(token);

            if (user != null) {
                UserDTO userDTO = new UserDTO();
                userDTO.setId(user.getId());
                userDTO.setUsername(user.getUsername());
                return ResponseEntity.ok(userDTO);
            } else {
                return ResponseEntity.status(401).body("Invalid token");
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Authentication failed");
        }
    }

    public static class TokenResponse {
        private String token;

        public TokenResponse(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
