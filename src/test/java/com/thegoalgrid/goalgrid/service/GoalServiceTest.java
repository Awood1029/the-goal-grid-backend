// File: test/java/com/thegoalgrid/goalgrid/service/GoalServiceTest.java
package com.thegoalgrid.goalgrid.service;

import com.thegoalgrid.goalgrid.entity.Board;
import com.thegoalgrid.goalgrid.entity.Goal;
import com.thegoalgrid.goalgrid.entity.User;
import com.thegoalgrid.goalgrid.repository.GoalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class GoalServiceTest {

    @InjectMocks
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private BoardService boardService;

    private User testUser;
    private Board testBoard;
    private Goal testGoal;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testBoard = new Board();
        testBoard.setId(10L);
        testBoard.setOwner(testUser);

        testGoal = new Goal();
        testGoal.setId(101L);
        testGoal.setDescription("Old Description");
        testGoal.setBoard(testBoard);
        testGoal.setCompleted(false);
    }

    @Test
    void testUpdateGoalDescription_Success() {
        when(goalRepository.findById(101L)).thenReturn(java.util.Optional.of(testGoal));
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> invocation.getArgument(0));
        String newDescription = "Updated Description";

        Goal updatedGoal = goalService.updateGoalDescription(101L, newDescription, testUser);
        assertThat(updatedGoal.getDescription()).isEqualTo(newDescription);
    }

    @Test
    void testUpdateGoalCompletionStatus_Success() {
        when(goalRepository.findById(101L)).thenReturn(java.util.Optional.of(testGoal));
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> invocation.getArgument(0));
        boolean newStatus = true;

        Goal updatedGoal = goalService.updateGoalCompletionStatus(101L, newStatus, testUser);
        assertThat(updatedGoal.isCompleted()).isEqualTo(newStatus);
    }
}
