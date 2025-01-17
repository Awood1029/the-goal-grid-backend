// File: main/java/com/thegoalgrid/goalgrid/service/GoalService.java
package com.thegoalgrid.goalgrid.service;

import com.thegoalgrid.goalgrid.entity.Board;
import com.thegoalgrid.goalgrid.entity.Goal;
import com.thegoalgrid.goalgrid.entity.User;
import com.thegoalgrid.goalgrid.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Nonnull;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final BoardService boardService;

    /**
     * Update the description of a specific goal.
     *
     * @param goalId      ID of the goal to update.
     * @param description New description for the goal.
     * @param user        The user performing the update.
     * @return Updated Goal.
     */
    @Transactional
    public Goal updateGoalDescription(@Nonnull Long goalId, @Nonnull String description, @Nonnull User user) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with ID: " + goalId));
        Board board = goal.getBoard();
        if (!board.getOwner().equals(user)) {
            throw new RuntimeException("User does not own this board.");
        }
        goal.setDescription(description);
        return goalRepository.save(goal);
    }

    /**
     * Update the completion status of a specific goal.
     *
     * @param goalId      ID of the goal to update.
     * @param isCompleted Completion status.
     * @param user        The user performing the update.
     * @return Updated Goal.
     */
    @Transactional
    public Goal updateGoalCompletionStatus(@Nonnull Long goalId, boolean isCompleted, @Nonnull User user) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with ID: " + goalId));
        Board board = goal.getBoard();
        if (!board.getOwner().equals(user)) {
            throw new RuntimeException("User does not own this board.");
        }
        goal.setCompleted(isCompleted);
        return goalRepository.save(goal);
    }
}
