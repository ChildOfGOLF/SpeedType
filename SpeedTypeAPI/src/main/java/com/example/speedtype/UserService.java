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
        System.out.println("UserService: Authenticating user: " + username);

        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            System.out.println("UserService: User found in database: " + user.getUsername());
            System.out.println("UserService: Stored password hash: " + user.getPassword().substring(0, 20) + "...");
            System.out.println("UserService: Input password: " + password);

            boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
            System.out.println("UserService: Password matches: " + passwordMatches);

            if (passwordMatches) {
                System.out.println("UserService: Authentication successful, generating token");
                return jwtUtil.generateToken(username);
            } else {
                System.out.println("UserService: Password does not match");
            }
        } else {
            System.out.println("UserService: User not found in database: " + username);
        }

        throw new RuntimeException("Invalid credentials!");
    }
}
