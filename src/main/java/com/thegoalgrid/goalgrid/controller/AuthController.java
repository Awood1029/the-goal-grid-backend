// main/java/com/thegoalgrid/goalgrid/controller/AuthController.java
package com.thegoalgrid.goalgrid.controller;

import com.thegoalgrid.goalgrid.dto.AuthResponseDTO;
import com.thegoalgrid.goalgrid.dto.LoginRequestDTO;
import com.thegoalgrid.goalgrid.dto.RegisterRequestDTO;
import com.thegoalgrid.goalgrid.entity.User;
import com.thegoalgrid.goalgrid.repository.UserRepository;
import com.thegoalgrid.goalgrid.security.JwtUtil;
import com.thegoalgrid.goalgrid.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        logger.info("Attempting to register user with username: {}", registerRequest.getUsername());
        if(userRepository.existsByUsername(registerRequest.getUsername())){
            logger.warn("Registration failed: Username {} is already taken", registerRequest.getUsername());
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }

        // Create new user's account
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());

        try {
            userRepository.save(user);
            logger.info("User {} successfully registered", registerRequest.getUsername());
        } catch (Exception e) {
            logger.error("Error saving user {}: {}", registerRequest.getUsername(), e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: Could not register user.");
        }

        // Generate access and refresh tokens
        String accessToken = jwtUtil.generateJwtToken(user.getId(), user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());
        AuthResponseDTO authResponse = new AuthResponseDTO(accessToken, refreshToken, user.getId(), user.getUsername(), user.getFirstName(), user.getLastName());

        // Return both tokens in a combined response
        Map<String, Object> response = new HashMap<>();
        response.put("auth", authResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest){
        logger.info("Attempting to authenticate user: {}", loginRequest.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String accessToken = jwtUtil.generateJwtToken(userDetails.getId(), userDetails.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(userDetails.getId(), userDetails.getUsername());
            logger.info("User {} successfully authenticated", loginRequest.getUsername());
            AuthResponseDTO authResponse = new AuthResponseDTO(accessToken, refreshToken, userDetails.getId(), userDetails.getUsername(), userDetails.getFirstName(), userDetails.getLastName());

            Map<String, Object> response = new HashMap<>();
            response.put("auth", authResponse);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            logger.warn("Authentication failed for user {}: {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Error: Invalid username or password");
        }
    }

    /**
     * New endpoint for refreshing access tokens using a refresh token.
     * Expects a JSON payload like: { "refreshToken": "..." }
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> tokenRequest) {
        String refreshToken = tokenRequest.get("refreshToken");
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body("Refresh token is missing");
        }

        // Validate the refresh token (assumes JwtUtil has a method for this)
        if (jwtUtil.validateRefreshToken(refreshToken)) {
            // Extract username from the refresh token (assumes such a method exists)
            String username = jwtUtil.getUsernameFromRefreshToken(refreshToken);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
            // Generate a new access token
            String newAccessToken = jwtUtil.generateJwtToken(user.getId(), user.getUsername());
            AuthResponseDTO authResponse = new AuthResponseDTO(newAccessToken, refreshToken, user.getId(), user.getUsername(), user.getFirstName(), user.getLastName());
            Map<String, Object> response = new HashMap<>();
            response.put("auth", authResponse);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token.");
        }
    }
}
