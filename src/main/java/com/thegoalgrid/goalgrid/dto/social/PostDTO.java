package com.thegoalgrid.goalgrid.dto.social;

import com.thegoalgrid.goalgrid.dto.UserDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDTO {
    private Long id;
    private UserDTO author;
    private String content;
    private LocalDateTime createdAt;

    private List<ReactionDTO> reactions;
    private List<CommentDTO> comments;

    private Long referencedGoalId;
    private boolean progressUpdate;
}
