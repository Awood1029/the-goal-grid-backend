package com.thegoalgrid.goalgrid.service;

import com.thegoalgrid.goalgrid.dto.social.CommentDTO;
import com.thegoalgrid.goalgrid.entity.Comment;
import com.thegoalgrid.goalgrid.entity.Goal;
import com.thegoalgrid.goalgrid.entity.User;
import com.thegoalgrid.goalgrid.mapper.CommentMapper;
import com.thegoalgrid.goalgrid.repository.CommentRepository;
import com.thegoalgrid.goalgrid.repository.GoalRepository;
import com.thegoalgrid.goalgrid.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserService userService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;

    /**
     * Update the description of a specific goal.
     */
    @Transactional
    public Goal updateGoalDescription(@Nonnull Long goalId, @Nonnull String description, @Nonnull User user) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with ID: " + goalId));
        if (!goal.getBoard().getOwner().equals(user)) {
            throw new RuntimeException("User does not own this board.");
        }
        goal.setDescription(description);
        return goalRepository.save(goal);
    }

    /**
     * Update the completion status of a specific goal.
     */
    @Transactional
    public Goal updateGoalCompletionStatus(@Nonnull Long goalId, boolean isCompleted, @Nonnull User user) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with ID: " + goalId));
        if (!goal.getBoard().getOwner().equals(user)) {
            throw new RuntimeException("User does not own this board.");
        }
        goal.setCompleted(isCompleted);
        return goalRepository.save(goal);
    }

    /**
     * Retrieve all comments attached directly to a goal.
     */
    public List<CommentDTO> getAllCommentsForGoal(Long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with ID: " + goalId));
        List<Comment> comments = goal.getComments();
        return comments.stream()
                .map(commentMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create a comment attached directly to a goal.
     */
    @Transactional
    public CommentDTO createCommentForGoal(Long goalId, CommentDTO commentDTO, UserDetailsImpl userDetails) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with ID: " + goalId));
        Comment comment = commentMapper.toEntity(commentDTO);
        User author = userService.getUserById(userDetails.getId());
        comment.setAuthor(author);
        comment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(comment);
        // Add the comment directly to the goal’s list of comments
        goal.getComments().add(comment);
        goalRepository.save(goal);
        return commentMapper.toDTO(comment);
    }
}
