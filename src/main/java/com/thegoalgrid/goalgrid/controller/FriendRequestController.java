package com.thegoalgrid.goalgrid.controller;

import com.thegoalgrid.goalgrid.dto.social.FriendRequestDTO;
import com.thegoalgrid.goalgrid.entity.FriendRequest;
import com.thegoalgrid.goalgrid.mapper.FriendRequestMapper;
import com.thegoalgrid.goalgrid.security.UserDetailsImpl;
import com.thegoalgrid.goalgrid.service.FriendRequestService;
import com.thegoalgrid.goalgrid.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/friend-requests")
@RequiredArgsConstructor
public class FriendRequestController {

    private final FriendRequestService friendRequestService;
    private final UserService userService;
    private final FriendRequestMapper friendRequestMapper;

    // Send a friend request to a user with the given recipientId
    @PostMapping("/send/{recipientId}")
    public ResponseEntity<?> sendFriendRequest(@PathVariable Long recipientId,
                                               Authentication authentication) {
        UserDetailsImpl currentUser = (UserDetailsImpl) authentication.getPrincipal();
        friendRequestService.sendRequest(
                userService.getUserById(currentUser.getId()),
                userService.getUserById(recipientId)
        );
        return ResponseEntity.ok("Friend request sent.");
    }

    // Get all pending friend requests for the logged-in user
    @GetMapping("/pending")
    public ResponseEntity<List<FriendRequestDTO>> getPendingRequests(Authentication authentication) {
        UserDetailsImpl currentUser = (UserDetailsImpl) authentication.getPrincipal();
        List<FriendRequest> pendingRequests = friendRequestService.getPendingRequests(
                userService.getUserById(currentUser.getId())
        );
        List<FriendRequestDTO> dtos = pendingRequests.stream()
                .map(friendRequestMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Accept a friend request by request ID
    @PostMapping("/accept/{requestId}")
    public ResponseEntity<?> acceptRequest(@PathVariable Long requestId,
                                           Authentication authentication) {
        UserDetailsImpl currentUser = (UserDetailsImpl) authentication.getPrincipal();
        friendRequestService.acceptRequest(
                requestId,
                userService.getUserById(currentUser.getId())
        );
        return ResponseEntity.ok("Friend request accepted.");
    }

    // Decline a friend request by request ID
    @PostMapping("/decline/{requestId}")
    public ResponseEntity<?> declineRequest(@PathVariable Long requestId,
                                            Authentication authentication) {
        UserDetailsImpl currentUser = (UserDetailsImpl) authentication.getPrincipal();
        friendRequestService.declineRequest(
                requestId,
                userService.getUserById(currentUser.getId())
        );
        return ResponseEntity.ok("Friend request declined.");
    }
}
