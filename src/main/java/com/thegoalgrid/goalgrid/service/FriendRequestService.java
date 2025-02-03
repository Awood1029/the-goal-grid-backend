package com.thegoalgrid.goalgrid.service;

import com.thegoalgrid.goalgrid.entity.FriendRequest;
import com.thegoalgrid.goalgrid.entity.FriendRequestStatus;
import com.thegoalgrid.goalgrid.entity.User;
import com.thegoalgrid.goalgrid.repository.FriendRequestRepository;
import com.thegoalgrid.goalgrid.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;

    @Transactional
    public void sendRequest(User sender, User recipient) {
        if (sender.equals(recipient)) {
            throw new RuntimeException("Cannot send friend request to yourself.");
        }
        if (friendRequestRepository.findBySenderAndRecipient(sender, recipient).isPresent()) {
            throw new RuntimeException("Friend request already sent.");
        }
        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setRecipient(recipient);
        friendRequestRepository.save(request);
    }

    public List<FriendRequest> getPendingRequests(User recipient) {
        return friendRequestRepository.findByRecipientAndStatus(recipient, FriendRequestStatus.PENDING);
    }

    @Transactional
    public void acceptRequest(Long requestId, User recipient) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found."));
        if (!request.getRecipient().equals(recipient)) {
            throw new RuntimeException("Unauthorized to accept this request.");
        }
        request.setStatus(FriendRequestStatus.ACCEPTED);
        friendRequestRepository.save(request);

        // Establish bidirectional friendship
        User sender = request.getSender();
        recipient.addFriend(sender);
        userRepository.save(sender);
        userRepository.save(recipient);
    }

    @Transactional
    public void declineRequest(Long requestId, User recipient) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found."));
        if (!request.getRecipient().equals(recipient)) {
            throw new RuntimeException("Unauthorized to decline this request.");
        }
        request.setStatus(FriendRequestStatus.DECLINED);
        friendRequestRepository.save(request);
    }

    // New method to check if a friend request is pending from sender to recipient.
    public boolean isFriendRequestPending(User sender, User recipient) {
        return friendRequestRepository.findBySenderAndRecipient(sender, recipient)
                .filter(req -> req.getStatus() == FriendRequestStatus.PENDING)
                .isPresent();
    }
}
