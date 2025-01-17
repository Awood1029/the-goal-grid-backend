// File: test/java/com/thegoalgrid/goalgrid/service/BoardServiceTest.java
package com.thegoalgrid.goalgrid.service;

import com.thegoalgrid.goalgrid.dto.board.GoalUpdateDTO;
import com.thegoalgrid.goalgrid.entity.Board;
import com.thegoalgrid.goalgrid.entity.Goal;
import com.thegoalgrid.goalgrid.entity.User;
import com.thegoalgrid.goalgrid.repository.BoardRepository;
import com.thegoalgrid.goalgrid.repository.GoalRepository;
import com.thegoalgrid.goalgrid.repository.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class BoardServiceTest {

    @InjectMocks
    private BoardService boardService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GroupRepository groupRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
    }

    @Test
    void testGetOrCreatePersonalBoard_WhenBoardExists() {
        Board existingBoard = new Board();
        existingBoard.setId(1L);
        existingBoard.setOwner(testUser);
        when(boardRepository.findByOwner(testUser)).thenReturn(existingBoard);

        Board board = boardService.getOrCreatePersonalBoard(testUser);
        assertThat(board).isEqualTo(existingBoard);
        verify(boardRepository, times(1)).findByOwner(testUser);
        verifyNoMoreInteractions(goalRepository);
    }

    @Test
    void testGetOrCreatePersonalBoard_WhenBoardDoesNotExist() {
        when(boardRepository.findByOwner(testUser)).thenReturn(null);
        Board createdBoard = new Board();
        createdBoard.setId(1L);
        createdBoard.setOwner(testUser);
        createdBoard.setGoals(new HashSet<>());
        when(boardRepository.save(any(Board.class))).thenReturn(createdBoard);

        Board board = boardService.getOrCreatePersonalBoard(testUser);

        // Verify that a board was created with 25 goals.
        assertThat(board.getOwner()).isEqualTo(testUser);
        assertThat(board.getGoals()).hasSize(25);
        verify(goalRepository, times(1)).saveAll(any(Set.class));
    }

    @Test
    void testUpdateBoardName() {
        Board board = new Board();
        board.setId(1L);
        board.setName("Old Name");
        board.setOwner(testUser);

        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        when(boardRepository.save(any(Board.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Board updatedBoard = boardService.updateBoardName(1L, "New Board Name", testUser.getId());
        assertThat(updatedBoard.getName()).isEqualTo("New Board Name");
    }

    @Test
    void testBulkUpdateGoals() {
        Board board = new Board();
        board.setId(1L);
        board.setOwner(testUser);
        Goal goal1 = new Goal();
        goal1.setId(101L);
        goal1.setDescription("Goal 1");
        goal1.setCompleted(false);
        goal1.setBoard(board);
        Goal goal2 = new Goal();
        goal2.setId(102L);
        goal2.setDescription("Goal 2");
        goal2.setCompleted(false);
        goal2.setBoard(board);
        board.setGoals(new HashSet<>(List.of(goal1, goal2)));

        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(boardRepository.save(any(Board.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GoalUpdateDTO update1 = new GoalUpdateDTO();
        update1.setGoalId(101L);
        update1.setDescription("Updated Goal 1");
        update1.setCompleted(true);
        GoalUpdateDTO update2 = new GoalUpdateDTO();
        update2.setGoalId(102L);
        update2.setDescription("Updated Goal 2");
        update2.setCompleted(true);

        Board updatedBoard = boardService.bulkUpdateGoals(1L, List.of(update1, update2), testUser.getId());

        ArgumentCaptor<Goal> goalCaptor = ArgumentCaptor.forClass(Goal.class);
        verify(goalRepository, times(2)).save(goalCaptor.capture());
        List<Goal> savedGoals = goalCaptor.getAllValues();

        assertThat(savedGoals).extracting(Goal::getDescription)
                .containsExactlyInAnyOrder("Updated Goal 1", "Updated Goal 2");

        assertThat(updatedBoard).isNotNull();
    }
}
