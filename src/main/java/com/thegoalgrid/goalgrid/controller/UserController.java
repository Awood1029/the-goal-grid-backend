// File: main/java/com/thegoalgrid/goalgrid/controller/UserController.java
package com.thegoalgrid.goalgrid.controller;

import com.thegoalgrid.goalgrid.dto.UserDTO;
import com.thegoalgrid.goalgrid.entity.User;
import com.thegoalgrid.goalgrid.mapper.UserMapper;
import com.thegoalgrid.goalgrid.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * Retrieve all users.
     *
     * @return List of UserDTOs.
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        try {
            logger.info("Fetching all users.");
            List<User> users = userService.getAllUsers();
            List<UserDTO> userDTOs = users.stream()
                    .map(userMapper::toDTO)
                    .collect(Collectors.toList());
            logger.info("Fetched {} users.", userDTOs.size());
            return ResponseEntity.ok(userDTOs);
        } catch (Exception e) {
            logger.error("Error fetching all users: {}", e.getMessage());
            throw e; // Let global exception handler manage it
        }
    }

    /**
     * Retrieve a user by ID.
     *
     * @param id User ID.
     * @return UserDTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable @Nonnull Long id) {
        try {
            logger.info("Fetching user with ID: {}", id);
            User user = userService.getUserById(id);
            UserDTO userDTO = userMapper.toDTO(user);
            logger.debug("Fetched User: {}", userDTO.getUsername());
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            logger.error("Error fetching user by ID {}: {}", id, e.getMessage());
            throw e; // Let global exception handler manage it
        }
    }
}
