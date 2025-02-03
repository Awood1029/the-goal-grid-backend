package com.thegoalgrid.goalgrid.controller;

import com.thegoalgrid.goalgrid.dto.social.UserProfileDTO;
import com.thegoalgrid.goalgrid.dto.board.BoardDTO;
import com.thegoalgrid.goalgrid.dto.GoalDTO;
import com.thegoalgrid.goalgrid.dto.social.PostDTO;
import com.thegoalgrid.goalgrid.mapper.UserProfileMapper;
import com.thegoalgrid.goalgrid.mapper.BoardMapper;
import com.thegoalgrid.goalgrid.entity.Goal;
import com.thegoalgrid.goalgrid.entity.User;
import com.thegoalgrid.goalgrid.service.PostService;
import com.thegoalgrid.goalgrid.service.UserService;
import com.thegoalgrid.goalgrid.service.FriendRequestService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final UserProfileMapper userProfileMapper;
    private final BoardMapper boardMapper;
    private final PostService postService;
    private final ModelMapper modelMapper;
    private final FriendRequestService friendRequestService;

    /**
     * Retrieves the profile of the currently authenticated user with enhanced info.
     * URL: GET /api/profile/me
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getCurrentUserProfile(@AuthenticationPrincipal com.thegoalgrid.goalgrid.security.UserDetailsImpl userDetails) {
        User user = userService.getUserById(userDetails.getId());
        UserProfileDTO profileDTO = userProfileMapper.toDTO(user);

        // Include user's board info if available
        if (user.getBoard() != null) {
            profileDTO.setBoard(boardMapper.toDTO(user.getBoard()));
            int completedCount = (int) user.getBoard().getGoals().stream()
                    .filter(Goal::isCompleted)
                    .count();
            profileDTO.setTotalCompletedGoals(completedCount);
            Goal recentCompletedGoal = user.getBoard().getGoals().stream()
                    .filter(Goal::isCompleted)
                    .max(Comparator.comparingLong(Goal::getId))
                    .orElse(null);
            if (recentCompletedGoal != null) {
                GoalDTO recentGoalDTO = modelMapper.map(recentCompletedGoal, GoalDTO.class);
                profileDTO.setRecentCompletedGoal(recentGoalDTO);
            }
        }

        // Retrieve the 3 most recent posts by this user
        List<PostDTO> recentPosts = postService.getRecentPostsByUser(user.getId());
        profileDTO.setRecentPosts(recentPosts);

        // For own profile, friend status is not applicable.
        profileDTO.setAreFriends(false);
        profileDTO.setFriendRequestPending(false);

        return ResponseEntity.ok(profileDTO);
    }

    /**
     * Retrieve a user profile by a specific user ID.
     * URL: GET /api/profile/{userId}
     * This method now includes friend status information if the requester is authenticated.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable Long userId, @AuthenticationPrincipal com.thegoalgrid.goalgrid.security.UserDetailsImpl currentUserDetails) {
        User profileUser = userService.getUserById(userId);
        UserProfileDTO profileDTO = userProfileMapper.toDTO(profileUser);

        // Include user's board info if available
        if (profileUser.getBoard() != null) {
            profileDTO.setBoard(boardMapper.toDTO(profileUser.getBoard()));
            int completedCount = (int) profileUser.getBoard().getGoals().stream()
                    .filter(Goal::isCompleted)
                    .count();
            profileDTO.setTotalCompletedGoals(completedCount);
            Goal recentCompletedGoal = profileUser.getBoard().getGoals().stream()
                    .filter(Goal::isCompleted)
                    .max(Comparator.comparingLong(Goal::getId))
                    .orElse(null);
            if (recentCompletedGoal != null) {
                GoalDTO recentGoalDTO = modelMapper.map(recentCompletedGoal, GoalDTO.class);
                profileDTO.setRecentCompletedGoal(recentGoalDTO);
            }
        }

        // Retrieve the 3 most recent posts by this user
        List<PostDTO> recentPosts = postService.getRecentPostsByUser(profileUser.getId());
        profileDTO.setRecentPosts(recentPosts);

        // If the current user is viewing someone else's profile, determine friend status.
        if (currentUserDetails != null && !currentUserDetails.getId().equals(userId)) {
            User currentUser = userService.getUserById(currentUserDetails.getId());
            // Check if they are already friends
            boolean areFriends = profileUser.getFriends().stream().anyMatch(friend -> friend.getId().equals(currentUser.getId()));
            profileDTO.setAreFriends(areFriends);
            // Check if a friend request is pending from current user to profile user
            boolean friendRequestPending = friendRequestService.isFriendRequestPending(currentUser, profileUser);
            profileDTO.setFriendRequestPending(friendRequestPending);
        } else {
            profileDTO.setAreFriends(false);
            profileDTO.setFriendRequestPending(false);
        }

        return ResponseEntity.ok(profileDTO);
    }
}
