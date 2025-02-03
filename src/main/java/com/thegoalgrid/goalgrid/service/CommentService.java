package com.thegoalgrid.goalgrid.service;

import com.thegoalgrid.goalgrid.dto.social.CommentDTO;
import com.thegoalgrid.goalgrid.dto.social.ReactionDTO;
import com.thegoalgrid.goalgrid.entity.Comment;
import com.thegoalgrid.goalgrid.entity.CommentReaction;
import com.thegoalgrid.goalgrid.entity.ReactionType;
import com.thegoalgrid.goalgrid.entity.User;
import com.thegoalgrid.goalgrid.mapper.CommentMapper;
import com.thegoalgrid.goalgrid.mapper.ReactionMapper;
import com.thegoalgrid.goalgrid.repository.CommentReactionRepository;
import com.thegoalgrid.goalgrid.repository.CommentRepository;
import com.thegoalgrid.goalgrid.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentReactionRepository commentReactionRepository;
    private final CommentMapper commentMapper;
    private final ReactionMapper reactionMapper;
    private final UserService userService;
    private final PostService postService;

    /**
     * Retrieve a comment by its ID.
     */
    public CommentDTO getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with ID: " + id));
        return commentMapper.toDTO(comment);
    }

    /**
     * Update a comment.
     * Only the comment owner is allowed to update. The provided UserDetailsImpl is used for authorization.
     */
    @Transactional
    public CommentDTO updateComment(Long id, CommentDTO commentDTO, UserDetailsImpl userDetails) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with ID: " + id));
        if (!comment.getAuthor().getId().equals(userDetails.getId())) {
            throw new RuntimeException("Unauthorized to update comment.");
        }
        // Only update allowed fields (e.g., content)
        if (commentDTO.getContent() != null) {
            comment.setContent(commentDTO.getContent());
        }
        commentRepository.save(comment);
        return commentMapper.toDTO(comment);
    }

    /**
     * Delete a comment.
     * Only the comment owner is allowed to perform deletion.
     */
    @Transactional
    public void deleteComment(Long id, UserDetailsImpl userDetails) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with ID: " + id));
        if (!comment.getAuthor().getId().equals(userDetails.getId())) {
            throw new RuntimeException("Unauthorized to delete comment.");
        }
        commentRepository.delete(comment);
    }

    /**
     * Create a reaction for a specific comment.
     * This method will be updated in the next section to include reaction restrictions.
     */
    @Transactional
    public ReactionDTO createReactionForComment(Long commentId, ReactionDTO reactionDTO, UserDetailsImpl userDetails) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with ID: " + commentId));
        User user = userService.getUserById(userDetails.getId());

        // Check if the user has already reacted with this type to this comment
        Optional<CommentReaction> existingReaction = commentReactionRepository.findByUserAndTypeAndComment(user, reactionDTO.getType(), comment);
        if (existingReaction.isPresent()) {
            throw new RuntimeException("You have already reacted with this type to this comment.");
        }

        // Create and save the new reaction
        CommentReaction reaction = reactionMapper.toCommentReactionEntity(reactionDTO);
        reaction.setUser(user);
        reaction.setComment(comment);
        CommentReaction savedReaction = commentReactionRepository.save(reaction);

        // Add the reaction to the comment's reactions
        comment.getCommentReactions().add(savedReaction);
        commentRepository.save(comment);

        return reactionMapper.toDTO(savedReaction);
    }

    /**
     * Remove a reaction for a specific comment.
     */
    @Transactional
    public void removeReactionForComment(Long commentId, ReactionType type, UserDetailsImpl userDetails) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with ID: " + commentId));
        User user = userService.getUserById(userDetails.getId());

        // Find the existing reaction
        CommentReaction reaction = commentReactionRepository.findByUserAndTypeAndComment(user, type, comment)
                .orElseThrow(() -> new RuntimeException("Reaction not found for type: " + type));

        // Remove the reaction
        comment.getCommentReactions().remove(reaction);
        commentReactionRepository.delete(reaction);
    }
}
