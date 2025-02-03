package com.thegoalgrid.goalgrid.controller;

import com.thegoalgrid.goalgrid.dto.PaginatedResponseDTO;
import com.thegoalgrid.goalgrid.dto.social.PostDTO;
import com.thegoalgrid.goalgrid.security.UserDetailsImpl;
import com.thegoalgrid.goalgrid.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final PostService postService;

    /**
     * Retrieve the main feed with optional sorting.
     */
    @GetMapping("/main")
    public ResponseEntity<PaginatedResponseDTO<PostDTO>> getMainFeed(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Page<PostDTO> postsPage = postService.getMainFeed(userDetails, page, size, sortBy, sortDir);
        PaginatedResponseDTO<PostDTO> response = new PaginatedResponseDTO<>(postsPage);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve feed for a specific group with optional sorting.
     */
    @GetMapping("/group/{groupUrl}")
    public ResponseEntity<PaginatedResponseDTO<PostDTO>> getGroupFeed(
            @PathVariable String groupUrl,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Page<PostDTO> postsPage = postService.getGroupFeed(groupUrl, page, size, sortBy, sortDir);
        PaginatedResponseDTO<PostDTO> response = new PaginatedResponseDTO<>(postsPage);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve feed for a specific goal with optional sorting.
     */
    @GetMapping("/goal/{goalId}")
    public ResponseEntity<PaginatedResponseDTO<PostDTO>> getGoalFeed(
            @PathVariable Long goalId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Page<PostDTO> postsPage = postService.getGoalFeed(goalId, page, size, sortBy, sortDir);
        PaginatedResponseDTO<PostDTO> response = new PaginatedResponseDTO<>(postsPage);
        return ResponseEntity.ok(response);
    }
}