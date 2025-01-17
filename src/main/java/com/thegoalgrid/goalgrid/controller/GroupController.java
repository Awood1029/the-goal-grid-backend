// File: main/java/com/thegoalgrid/goalgrid/controller/GroupController.java
package com.thegoalgrid.goalgrid.controller;

import com.thegoalgrid.goalgrid.dto.GroupDTO;
import com.thegoalgrid.goalgrid.dto.JoinGroupRequestDTO;
import com.thegoalgrid.goalgrid.dto.board.BoardDTO;
import com.thegoalgrid.goalgrid.entity.Board;
import com.thegoalgrid.goalgrid.entity.Group;
import com.thegoalgrid.goalgrid.entity.User;
import com.thegoalgrid.goalgrid.mapper.BoardMapper;
import com.thegoalgrid.goalgrid.mapper.GroupMapper;
import com.thegoalgrid.goalgrid.security.UserDetailsImpl;
import com.thegoalgrid.goalgrid.service.GroupService;
import com.thegoalgrid.goalgrid.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final UserService userService;
    private final GroupMapper groupMapper;
    private final BoardMapper boardMapper;
    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

    @PostMapping
    public ResponseEntity<GroupDTO> createGroup(
            @RequestParam @Nonnull String name,
            Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getId();
            logger.info("User {} is creating a new group with name: {}", userId, name);

            User user = userService.getUserById(userId);
            Group group = groupService.createGroup(user, name);
            logger.info("Group {} created with unique URL: {}", group.getId(), group.getUniqueUrl());

            GroupDTO groupDTO = groupMapper.toDTO(group);
            return ResponseEntity.ok(groupDTO);
        } catch (Exception e) {
            logger.error("Error creating group: {}", e.getMessage());
            throw new RuntimeException("Failed to create group: " + e.getMessage());
        }
    }

    @PostMapping("/join")
    public ResponseEntity<GroupDTO> joinGroup(
            @RequestBody @Nonnull JoinGroupRequestDTO requestDTO,
            Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getId();
            logger.info("User {} attempting to join group with invite code: {}", userId, requestDTO.getInviteCode());

            Group updatedGroup = groupService.addUserToGroupByInviteCode(requestDTO.getInviteCode(), userId);
            GroupDTO groupDTO = groupMapper.toDTO(updatedGroup);
            return ResponseEntity.ok(groupDTO);
        } catch (Exception e) {
            logger.error("Error joining group: {}", e.getMessage());
            throw new RuntimeException("Failed to join group: " + e.getMessage());
        }
    }

    @GetMapping("/{uniqueUrl}")
    public ResponseEntity<GroupDTO> getGroupByUniqueUrl(@PathVariable @Nonnull String uniqueUrl) {
        try {
            logger.info("Fetching group with unique URL: {}", uniqueUrl);
            Group group = groupService.getGroupByUniqueUrl(uniqueUrl)
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            GroupDTO groupDTO = groupMapper.toDTO(group);
            return ResponseEntity.ok(groupDTO);
        } catch (Exception e) {
            logger.error("Error fetching group by unique URL: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch group: " + e.getMessage());
        }
    }

    @GetMapping("/invite/{inviteCode}")
    public ResponseEntity<GroupDTO> getGroupByInviteCode(@PathVariable @Nonnull String inviteCode) {
        try {
            logger.info("Fetching group with invite code: {}", inviteCode);
            Group group = groupService.getGroupByInviteCode(inviteCode)
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            GroupDTO groupDTO = groupMapper.toDTO(group);
            return ResponseEntity.ok(groupDTO);
        } catch (Exception e) {
            logger.error("Error fetching group by invite code: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch group: " + e.getMessage());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<List<GroupDTO>> getGroupsForUser(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        List<Group> groups = groupService.getGroupsByUser(userId);
        List<GroupDTO> dtos = groups.stream().map(groupMapper::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{uniqueUrl}/boards")
    public ResponseEntity<List<BoardDTO>> getBoardsForGroup(@PathVariable @Nonnull String uniqueUrl, Authentication authentication) {
        List<Board> boards = groupService.getBoards(uniqueUrl);
        List<BoardDTO> boardDtos = new ArrayList<>();
        if(boards != null) {
            for (Board board : boards) {
                boardDtos.add(boardMapper.toDTO(board));
            }
        }
        return ResponseEntity.ok(boardDtos);
    }
}
