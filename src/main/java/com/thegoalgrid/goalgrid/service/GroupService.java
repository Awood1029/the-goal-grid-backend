// File: main/java/com/thegoalgrid/goalgrid/service/GroupService.java
package com.thegoalgrid.goalgrid.service;

import com.thegoalgrid.goalgrid.entity.Board;
import com.thegoalgrid.goalgrid.entity.Group;
import com.thegoalgrid.goalgrid.entity.User;
import com.thegoalgrid.goalgrid.repository.GroupRepository;
import com.thegoalgrid.goalgrid.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final BoardService boardService;

    /**
     * Create a new group for the user.
     * Automatically adds the user to the group.
     */
    @Transactional
    public Group createGroup(User user, String name) {
        Group group = new Group();
        group.setName(name);
        group.addUser(user);
        groupRepository.save(group);
        return group;
    }

    /**
     * Add a user to a group using an invite code.
     * If the user does not have a personal board yet, create one.
     */
    @Transactional
    public Group addUserToGroupByInviteCode(@Nonnull String inviteCode, @Nonnull Long userId) {
        Group group = groupRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new RuntimeException("Group not found with invite code: " + inviteCode));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Ensure the user has a personal board
        if(user.getBoard() == null) {
            boardService.getOrCreatePersonalBoard(user);
        }

        if (group.getUsers().contains(user)) {
            throw new RuntimeException("User is already a member of the group.");
        }
        group.addUser(user);
        groupRepository.save(group);
        return group;
    }

    /**
     * Get a group by unique URL.
     */
    public Optional<Group> getGroupByUniqueUrl(@Nonnull String uniqueUrl) {
        return groupRepository.findByUniqueUrl(uniqueUrl);
    }

    /**
     * Get a group by invite code.
     */
    public Optional<Group> getGroupByInviteCode(@Nonnull String inviteCode) {
        return groupRepository.findByInviteCode(inviteCode);
    }

    /**
     * Get all groups that a specific user is a member of.
     */
    public List<Group> getGroupsByUser(@Nonnull Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return new ArrayList<>(user.getGroups());
    }

    public List<Board> getBoards(@Nonnull String uniqueUrl) {
        Group group = groupRepository.findByUniqueUrl(uniqueUrl)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        List<Board> boards = new ArrayList<>();
        for (User user : group.getUsers()) {
            boards.add(user.getBoard());
        }
        return boards;
    }
}
