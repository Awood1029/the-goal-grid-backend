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

        // Generate JWT token (optional, if you want to auto-login, but per spec we return to login)
        String token = jwtUtil.generateJwtToken(user.getId(), user.getUsername());
        AuthResponseDTO authResponse = new AuthResponseDTO(token, user.getId(), user.getUsername(), user.getFirstName(), user.getLastName());
        // Instead of auto-login, simply log registration and instruct user to login.
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest){
        logger.info("Attempting to authenticate user: {}", loginRequest.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String token = jwtUtil.generateJwtToken(userDetails.getId(), userDetails.getUsername());
            logger.info("User {} successfully authenticated", loginRequest.getUsername());
            AuthResponseDTO authResponse = new AuthResponseDTO(token, userDetails.getId(), userDetails.getUsername(), userDetails.getFirstName(), userDetails.getLastName());
            return ResponseEntity.ok(authResponse);
        } catch (AuthenticationException e) {
            logger.warn("Authentication failed for user {}: {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Error: Invalid username or password");
        }
    }
}
