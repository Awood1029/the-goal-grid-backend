// File: main/java/com/thegoalgrid/goalgrid/service/BoardService.java
package com.thegoalgrid.goalgrid.service;

import com.thegoalgrid.goalgrid.dto.board.GoalUpdateDTO;
import com.thegoalgrid.goalgrid.entity.Board;
import com.thegoalgrid.goalgrid.entity.Goal;
import com.thegoalgrid.goalgrid.entity.Group;
import com.thegoalgrid.goalgrid.entity.User;
import com.thegoalgrid.goalgrid.repository.BoardRepository;
import com.thegoalgrid.goalgrid.repository.GroupRepository;
import com.thegoalgrid.goalgrid.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final GoalRepository goalRepository;
    private final GroupRepository groupRepository;

    /**
     * Get or create a personal board for the given user.
     * This method returns the existing board if one exists; otherwise it creates
     * a new board with default goals.
     */
    @Transactional
    public Board getOrCreatePersonalBoard(@Nonnull User owner) {
        Board board = boardRepository.findByOwner(owner);

        if(board != null) {
            return board;
        }
        // Create new board with default goals
        board = new Board();
        board.setOwner(owner);
        board.setName("Board of " + owner.getUsername());
        board.setCompletedRows(0);
        board.setCompletedDiagonals(0);
        board = boardRepository.save(board);

        Set<Goal> goals = new HashSet<>();
        for (int i = 1; i <= 25; i++) {
            String description = (i == 13) ? "FREE SPACE" : "Goal " + i;
            Goal goal = new Goal();
            goal.setDescription(description);
            goal.setPosition(i);
            goal.setCompleted(i == 13);
            goal.setBoard(board);
            goals.add(goal);
        }
        goalRepository.saveAll(goals);
        board.setGoals(goals);
        return boardRepository.save(board);
    }

    /**
     * Update the board name.
     */
    public Board updateBoardName(Long boardId, String boardName, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));
        board.setName(boardName);
        return boardRepository.save(board);
    }

    /**
     * Bulk update goals for a board.
     */
    @Transactional
    public Board bulkUpdateGoals(Long boardId, List<GoalUpdateDTO> goalUpdates, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));
        for (GoalUpdateDTO update : goalUpdates) {
            Optional<Goal> optGoal = board.getGoals().stream()
                    .filter(g -> g.getId().equals(update.getGoalId()))
                    .findFirst();
            if (optGoal.isPresent()) {
                Goal goal = optGoal.get();
                if (update.getDescription() != null) {
                    goal.setDescription(update.getDescription());
                }
                if (update.getCompleted() != null) {
                    goal.setCompleted(update.getCompleted());
                }
                goalRepository.save(goal);
            }
        }
        return boardRepository.save(board);
    }
}
