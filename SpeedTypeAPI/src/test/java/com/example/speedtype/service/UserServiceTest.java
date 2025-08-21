package com.example.speedtype.service;

import com.example.speedtype.entity.User;
import com.example.speedtype.repository.UserRepository;
import com.example.speedtype.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_ReturnsToken_WhenUserIsNew() {
        String username = "testuser";
        String password = "password";
        String encodedPassword = "encodedPassword";
        String token = "jwtToken";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(jwtUtil.generateToken(username)).thenReturn(token);

        String result = userService.registerUser(username, password);

        assertEquals(token, result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_ThrowsException_WhenUserExists() {
        String username = "testuser";
        String password = "password";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.registerUser(username, password));
        assertEquals("Username already exists!", exception.getMessage());
    }

    @Test
    void authenticateUser_ReturnsToken_WhenCredentialsAreValid() {
        String username = "testuser";
        String password = "password";
        String encodedPassword = "encodedPassword";
        String token = "jwtToken";
        User user = new User(username, encodedPassword);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtUtil.generateToken(username)).thenReturn(token);

        String result = userService.authenticateUser(username, password);
        assertEquals(token, result);
    }

    @Test
    void authenticateUser_ThrowsException_WhenPasswordIsInvalid() {
        String username = "testuser";
        String password = "wrongPassword";
        String encodedPassword = "encodedPassword";
        User user = new User(username, encodedPassword);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.authenticateUser(username, password));
        assertEquals("Invalid credentials!", exception.getMessage());
    }

    @Test
    void authenticateUser_ThrowsException_WhenUserNotFound() {
        String username = "notfound";
        String password = "password";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.authenticateUser(username, password));
        assertEquals("Invalid credentials!", exception.getMessage());
    }
}
