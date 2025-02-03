package com.thegoalgrid.goalgrid.controller;

import com.thegoalgrid.goalgrid.dto.social.CommentDTO;
import com.thegoalgrid.goalgrid.dto.social.PostDTO;
import com.thegoalgrid.goalgrid.dto.social.ReactionDTO;
import com.thegoalgrid.goalgrid.entity.ReactionType;
import com.thegoalgrid.goalgrid.security.UserDetailsImpl;
import com.thegoalgrid.goalgrid.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * Create a new post.
     * Example: POST /api/posts
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<PostDTO> createPost(@RequestBody PostDTO postDTO, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        PostDTO createdPost = postService.createPost(postDTO, userDetails);
        return ResponseEntity.ok(createdPost);
    }

    /**
     * Get a post by ID.
     * Example: GET /api/posts/{postId}
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long postId) {
        PostDTO postDTO = postService.getPostById(postId);
        return ResponseEntity.ok(postDTO);
    }

    /**
     * Update a post by its ID.
     * Only the post author is authorized.
     * Example: PUT /api/posts/{postId}
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{postId}")
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable Long postId,
            @RequestBody PostDTO postDTO,
            Authentication authentication
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        PostDTO updatedPost = postService.updatePost(postId, postDTO, userDetails);
        return ResponseEntity.ok(updatedPost);
    }

    /**
     * Delete a post by its ID.
     * Only the post author is authorized.
     * Example: DELETE /api/posts/{postId}
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        postService.deletePost(postId, userDetails);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all comments for a specific post.
     * Example: GET /api/posts/{postId}/comments
     */
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentDTO>> getAllCommentsForPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        List<CommentDTO> comments = postService.getAllCommentsForPost(postId, sortBy, sortDir);
        return ResponseEntity.ok(comments);
    }

    /**
     * Create a new comment on a specific post.
     * Example: POST /api/posts/{postId}/comments
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentDTO> createCommentForPost(
            @PathVariable Long postId,
            @RequestBody CommentDTO commentDTO,
            Authentication authentication
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        CommentDTO createdComment = postService.createCommentForPost(postId, commentDTO, userDetails);
        return ResponseEntity.ok(createdComment);
    }

    /**
     * Add a reaction to a specific post.
     * Example: POST /api/posts/{postId}/reactions
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{postId}/reactions")
    public ResponseEntity<ReactionDTO> createReactionForPost(
            @PathVariable Long postId,
            @RequestBody ReactionDTO reactionDTO,
            Authentication authentication
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ReactionDTO createdReaction = postService.createReactionForPost(postId, reactionDTO, userDetails);
        return ResponseEntity.ok(createdReaction);
    }

    /**
     * Remove a reaction from a specific post.
     * Example: DELETE /api/posts/{postId}/reactions?type=LIKE
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{postId}/reactions")
    public ResponseEntity<Void> removeReactionFromPost(
            @PathVariable Long postId,
            @RequestParam ReactionType type,
            Authentication authentication
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        postService.removeReactionForPost(postId, type, userDetails);
        return ResponseEntity.noContent().build();
    }
}
