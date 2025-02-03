package com.thegoalgrid.goalgrid.controller;

import com.thegoalgrid.goalgrid.entity.User;
import com.thegoalgrid.goalgrid.repository.UserRepository;
import com.thegoalgrid.goalgrid.security.UserDetailsImpl;
import com.thegoalgrid.goalgrid.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final UserService userService;
    private final UserRepository userRepository;

    /**
     * Endpoint to remove a friend.
     * URL: POST /api/friends/remove/{friendId}
     */
    @PostMapping("/remove/{friendId}")
    public ResponseEntity<?> removeFriend(@PathVariable Long friendId, Authentication authentication) {
        UserDetailsImpl currentUserDetails = (UserDetailsImpl) authentication.getPrincipal();
        User currentUser = userService.getUserById(currentUserDetails.getId());
        User friend = userService.getUserById(friendId);

        // Remove friend bidirectionally
        currentUser.removeFriend(friend);
        userRepository.save(currentUser);
        userRepository.save(friend);

        return ResponseEntity.ok("Friend removed.");
    }
}
