package com.thegoalgrid.goalgrid.controller;

import com.thegoalgrid.goalgrid.dto.social.CommentDTO;
import com.thegoalgrid.goalgrid.dto.social.ReactionDTO;
import com.thegoalgrid.goalgrid.entity.ReactionType;
import com.thegoalgrid.goalgrid.security.UserDetailsImpl;
import com.thegoalgrid.goalgrid.entity.User;
import com.thegoalgrid.goalgrid.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * Get a single comment by its own ID.
     * Example: GET /api/comments/{commentId}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Long id) {
        CommentDTO commentDTO = commentService.getCommentById(id);
        return ResponseEntity.ok(commentDTO);
    }

    /**
     * Update a comment by its own ID.
     * Only the comment owner is authorized.
     * Example: PUT /api/comments/{commentId}
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable Long id,
            @RequestBody CommentDTO commentDTO,
            Authentication authentication
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        CommentDTO updatedComment = commentService.updateComment(id, commentDTO, userDetails);
        return ResponseEntity.ok(updatedComment);
    }

    /**
     * Delete a comment by its own ID.
     * Only the comment owner is authorized.
     * Example: DELETE /api/comments/{commentId}
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        commentService.deleteComment(id, userDetails);
        return ResponseEntity.noContent().build();
    }

    /**
     * Add a reaction to a specific comment.
     * Example: POST /api/comments/{commentId}/reactions
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{commentId}/reactions")
    public ResponseEntity<ReactionDTO> createReactionForComment(
            @PathVariable Long commentId,
            @RequestBody ReactionDTO reactionDTO,
            Authentication authentication
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ReactionDTO createdReaction = commentService.createReactionForComment(commentId, reactionDTO, userDetails);
        return ResponseEntity.ok(createdReaction);
    }

    /**
     * Remove a reaction from a specific comment.
     * Example: DELETE /api/comments/{commentId}/reactions?type=LOVE
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{commentId}/reactions")
    public ResponseEntity<Void> removeReactionFromComment(
            @PathVariable Long commentId,
            @RequestParam ReactionType type,
            Authentication authentication
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        commentService.removeReactionForComment(commentId, type, userDetails);
        return ResponseEntity.noContent().build();
    }
}
