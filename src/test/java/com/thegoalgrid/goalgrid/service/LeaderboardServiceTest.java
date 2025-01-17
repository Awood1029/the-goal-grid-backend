// File: test/java/com/thegoalgrid/goalgrid/service/LeaderboardServiceTest.java
package com.thegoalgrid.goalgrid.service;

import com.thegoalgrid.goalgrid.entity.Board;
import com.thegoalgrid.goalgrid.entity.Group;
import com.thegoalgrid.goalgrid.repository.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class LeaderboardServiceTest {

    @InjectMocks
    private LeaderboardService leaderboardService;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupService groupService;

    private Group group1;
    private Group group2;

    @BeforeEach
    void setup() {
        group1 = new Group();
        group1.setId(1L);
        group1.setUniqueUrl("url1");

        group2 = new Group();
        group2.setId(2L);
        group2.setUniqueUrl("url2");

        // Create boards for each group via the groupService.getBoards() method simulation.
        Board board1 = new Board();
        board1.setCompletedRows(1);
        board1.setCompletedDiagonals(1);

        Board board2 = new Board();
        board2.setCompletedRows(2);
        board2.setCompletedDiagonals(1);

        when(groupService.getBoards("url1")).thenReturn(List.of(board1));
        when(groupService.getBoards("url2")).thenReturn(List.of(board2));

        when(groupRepository.findAll()).thenReturn(List.of(group1, group2));
    }

    @Test
    void testGetLeaderboard() {
        List<Group> leaderboard = leaderboardService.getLeaderboard();
        // Group2 should appear first due to higher total completions.
        assertThat(leaderboard.get(0).getId()).isEqualTo(2L);
    }
}
