// File: test/java/com/thegoalgrid/goalgrid/service/GroupServiceTest.java
package com.thegoalgrid.goalgrid.service;

import com.thegoalgrid.goalgrid.entity.Board;
import com.thegoalgrid.goalgrid.entity.Group;
import com.thegoalgrid.goalgrid.entity.User;
import com.thegoalgrid.goalgrid.repository.GroupRepository;
import com.thegoalgrid.goalgrid.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.Optional;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class GroupServiceTest {

    @InjectMocks
    private GroupService groupService;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BoardService boardService;

    private User testUser;
    private Group testGroup;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testGroup = new Group();
        testGroup.setId(100L);
        testGroup.setName("Test Group");
        testGroup.setUniqueUrl("unique-url");
    }

    @Test
    void testCreateGroup() {
        when(groupRepository.save(any(Group.class))).thenReturn(testGroup);
        Group createdGroup = groupService.createGroup(testUser, "Test Group");
        assertThat(createdGroup.getName()).isEqualTo("Test Group");
    }

    @Test
    void testAddUserToGroupByInviteCode() {
        when(groupRepository.findByInviteCode("INV12345")).thenReturn(Optional.of(testGroup));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(boardService.getOrCreatePersonalBoard(testUser)).thenReturn(new Board());

        Group updatedGroup = groupService.addUserToGroupByInviteCode("INV12345", 1L);
        assertThat(updatedGroup).isNotNull();
    }

    @Test
    void testGetGroupsByUser() {
        testUser.setGroups(new java.util.HashSet<>());
        testUser.getGroups().add(testGroup);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        java.util.List<Group> groups = groupService.getGroupsByUser(1L);
        assertThat(groups).hasSize(1);
    }
}
