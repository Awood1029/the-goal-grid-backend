// File: main/java/com/thegoalgrid/goalgrid/controller/BoardController.java
package com.thegoalgrid.goalgrid.controller;

import com.thegoalgrid.goalgrid.dto.board.BoardDTO;
import com.thegoalgrid.goalgrid.dto.board.BoardNameDTO;
import com.thegoalgrid.goalgrid.dto.board.GoalUpdateDTO;
import com.thegoalgrid.goalgrid.entity.Board;
import com.thegoalgrid.goalgrid.entity.Goal;
import com.thegoalgrid.goalgrid.entity.User;
import com.thegoalgrid.goalgrid.mapper.BoardMapper;
import com.thegoalgrid.goalgrid.repository.BoardRepository;
import com.thegoalgrid.goalgrid.repository.GoalRepository;
import com.thegoalgrid.goalgrid.security.UserDetailsImpl;
import com.thegoalgrid.goalgrid.service.BoardService;
import com.thegoalgrid.goalgrid.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final UserService userService;
    private final BoardMapper boardMapper;
    private final BoardRepository boardRepository;
    private final GoalRepository goalRepository;

    private static final Logger logger = LoggerFactory.getLogger(BoardController.class);

    /**
     * Get all boards for the authenticated user.
     */
    @GetMapping
    public ResponseEntity<BoardDTO> getUserBoard(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        User user = userService.getUserById(userId);

        Board board = boardService.getOrCreatePersonalBoard(user);
        BoardDTO dto = boardMapper.toDTO(board);

        return ResponseEntity.ok(dto);
    }

    /**
     * Create a new board for the authenticated user.
     */
    @PostMapping
    public ResponseEntity<BoardDTO> createBoard(@RequestBody @Nonnull BoardNameDTO requestDTO,
                                                Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getId();
            logger.debug("Creating board for user {}", userId);

            User user = userService.getUserById(userId);

            // Create and save the board first
            Board board = new Board();
            board.setOwner(user);
            board.setName(requestDTO.getBoardName());
            board = boardRepository.save(board);

            // Create and add goals (using FREE SPACE for position 13)
            Set<Goal> goals = new HashSet<>();
            for (int i = 1; i <= 25; i++) {
                Goal goal = new Goal();
                goal.setDescription(i == 13 ? "FREE SPACE" : "Goal " + i);
                goal.setPosition(i);
                goal.setCompleted(i == 13);
                board.addGoal(goal);
                goals.add(goal);
            }
            goalRepository.saveAll(goals);
            board = boardRepository.save(board);

            BoardDTO boardDTO = boardMapper.toDTO(board);
            return ResponseEntity.ok(boardDTO);

        } catch (Exception e) {
            logger.error("Error creating board: ", e);
            throw new RuntimeException("Failed to create board: " + e.getMessage());
        }
    }

    /**
     * Update the board name.
     */
    @PutMapping("/{boardId}/name")
    public ResponseEntity<BoardDTO> updateBoardName(@PathVariable Long boardId,
                                                    @RequestBody @Nonnull BoardNameDTO requestDTO,
                                                    Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getId();
            logger.debug("User {} updating board {} name to {}", userId, boardId, requestDTO.getBoardName());

            Board board = boardService.updateBoardName(boardId, requestDTO.getBoardName(), userId);
            BoardDTO boardDTO = boardMapper.toDTO(board);
            return ResponseEntity.ok(boardDTO);
        } catch (Exception e) {
            logger.error("Error updating board name: {}", e.getMessage());
            throw new RuntimeException("Failed to update board name: " + e.getMessage());
        }
    }

    /**
     * Bulk update goals for a board.
     */
    @PutMapping("/{boardId}/goals")
    public ResponseEntity<BoardDTO> updateBoardGoals(@PathVariable Long boardId,
                                                     @RequestBody @Nonnull List<GoalUpdateDTO> goalUpdates,
                                                     Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getId();
            logger.debug("User {} bulk updating goals for board {}", userId, boardId);

            Board board = boardService.bulkUpdateGoals(boardId, goalUpdates, userId);
            BoardDTO boardDTO = boardMapper.toDTO(board);
            return ResponseEntity.ok(boardDTO);
        } catch (Exception e) {
            logger.error("Error bulk updating goals: {}", e.getMessage());
            throw new RuntimeException("Failed to update board goals: " + e.getMessage());
        }
    }
}
