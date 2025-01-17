// File: main/java/com/thegoalgrid/goalgrid/controller/GoalController.java
package com.thegoalgrid.goalgrid.controller;

import com.thegoalgrid.goalgrid.dto.GoalDTO;
import com.thegoalgrid.goalgrid.entity.Goal;
import com.thegoalgrid.goalgrid.entity.User;
import com.thegoalgrid.goalgrid.service.GoalService;
import com.thegoalgrid.goalgrid.service.UserService;
import com.thegoalgrid.goalgrid.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Nonnull;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(GoalController.class);

    /**
     * Update the description of a specific goal.
     */
    @PutMapping("/{goalId}/description")
    public ResponseEntity<GoalDTO> updateGoalDescription(
            @PathVariable @Nonnull Long goalId,
            @RequestParam @Nonnull String description,
            Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getId();
            logger.info("User {} updating description of goal {}", userId, goalId);

            User user = userService.getUserById(userId);
            Goal updatedGoal = goalService.updateGoalDescription(goalId, description, user);

            GoalDTO goalDTO = modelMapper.map(updatedGoal, GoalDTO.class);
            goalDTO.setBoardId(updatedGoal.getBoard().getId());
            return ResponseEntity.ok(goalDTO);
        } catch (Exception e) {
            logger.error("Error updating goal description: {}", e.getMessage());
            throw new RuntimeException("Failed to update goal description: " + e.getMessage());
        }
    }

    /**
     * Update the completion status of a specific goal.
     */
    @PutMapping("/{goalId}/status")
    public ResponseEntity<GoalDTO> updateGoalStatus(
            @PathVariable @Nonnull Long goalId,
            @RequestParam boolean isCompleted,
            Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getId();
            logger.info("User {} updating status of goal {} to {}", userId, goalId, isCompleted);

            User user = userService.getUserById(userId);
            Goal updatedGoal = goalService.updateGoalCompletionStatus(goalId, isCompleted, user);

            GoalDTO goalDTO = modelMapper.map(updatedGoal, GoalDTO.class);
            goalDTO.setBoardId(updatedGoal.getBoard().getId());
            return ResponseEntity.ok(goalDTO);
        } catch (Exception e) {
            logger.error("Error updating goal status: {}", e.getMessage());
            throw new RuntimeException("Failed to update goal status: " + e.getMessage());
        }
    }
}
