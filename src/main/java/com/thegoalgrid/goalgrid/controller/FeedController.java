package com.thegoalgrid.goalgrid.controller;

import com.thegoalgrid.goalgrid.dto.social.PostDTO;
import com.thegoalgrid.goalgrid.security.UserDetailsImpl;
import com.thegoalgrid.goalgrid.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final PostService postService;

    /**
     * Retrieve the main feed.
     */
    @GetMapping("/main")
    public List<PostDTO> getMainFeed(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return postService.getMainFeed(userDetails);
    }

    /**
     * Retrieve feed for a specific group.
     */
    @GetMapping("/group/{groupUrl}")
    public List<PostDTO> getGroupFeed(@PathVariable String groupUrl) {
        return postService.getGroupFeed(groupUrl);
    }

    /**
     * Retrieve feed for a specific goal.
     * Returns only posts that reference the given goal.
     */
    @GetMapping("/goal/{goalId}")
    public List<PostDTO> getGoalFeed(@PathVariable Long goalId) {
        return postService.getGoalFeed(goalId);
    }
}
