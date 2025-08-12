package com.example.speedtype;

import com.example.speedtype.User;
import com.example.speedtype.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String registerUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists!");
        }
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(username, encodedPassword);
        userRepository.save(user);
        return jwtUtil.generateToken(username);
    }
    
    public String authenticateUser(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());

            if (passwordMatches) {
                return jwtUtil.generateToken(username);
            }
        }

        throw new RuntimeException("Invalid credentials!");
    }

    public User getUserFromToken(String token) {
        try {
            String username = jwtUtil.extractUsername(token);
            if (username != null && jwtUtil.validateToken(token, username)) {
                Optional<User> userOptional = userRepository.findByUsername(username);
                return userOptional.orElse(null);
            }
        } catch (Exception e) {
            System.err.println("Error extracting user from token: " + e.getMessage());
        }
        return null;
    }
}
