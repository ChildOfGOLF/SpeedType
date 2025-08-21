package com.example.speedtype.controller;

import com.example.speedtype.dto.UserDTO;
import com.example.speedtype.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private AuthController authController;

    @Test
    void register_ReturnsToken_WhenRegistrationSuccessful() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password");
        String token = "jwtToken";
        when(userService.registerUser(userDTO.getUsername(), userDTO.getPassword())).thenReturn(token);

        ResponseEntity<?> response = authController.register(userDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof AuthController.TokenResponse);
        AuthController.TokenResponse tokenResponse = (AuthController.TokenResponse) response.getBody();
        assertEquals(token, tokenResponse.getToken());
    }

    @Test
    void register_ReturnsBadRequest_WhenUserExists() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password");
        when(userService.registerUser(userDTO.getUsername(), userDTO.getPassword())).thenThrow(new RuntimeException("Username already exists!"));

        ResponseEntity<?> response = authController.register(userDTO);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Username already exists!", response.getBody());
    }

    @Test
    void login_ReturnsToken_WhenLoginSuccessful() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password");
        String token = "jwtToken";
        when(userService.authenticateUser(userDTO.getUsername(), userDTO.getPassword())).thenReturn(token);

        ResponseEntity<?> response = authController.login(userDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof AuthController.TokenResponse);
        AuthController.TokenResponse tokenResponse = (AuthController.TokenResponse) response.getBody();
        assertEquals(token, tokenResponse.getToken());
    }

    @Test
    void login_ReturnsUnauthorized_WhenLoginFails() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("wrongpassword");
        when(userService.authenticateUser(userDTO.getUsername(), userDTO.getPassword())).thenThrow(new RuntimeException("Invalid credentials!"));

        ResponseEntity<?> response = authController.login(userDTO);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid credentials!", response.getBody());
    }
}
