package com.thegoalgrid.goalgrid.dto.social;

import com.thegoalgrid.goalgrid.dto.UserDTO;
import com.thegoalgrid.goalgrid.dto.board.BoardDTO;
import com.thegoalgrid.goalgrid.dto.GoalDTO;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class UserProfileDTO {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    // Friend list (using existing UserDTO)
    private Set<UserDTO> friends;

    private BoardDTO board;
    private GoalDTO recentCompletedGoal;
    private Integer totalCompletedGoals;
    private List<PostDTO> recentPosts;

    // New fields for friend status
    private boolean areFriends;
    private boolean friendRequestPending;
}
