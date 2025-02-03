package com.thegoalgrid.goalgrid.dto.social;

import com.thegoalgrid.goalgrid.dto.UserDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentDTO {
    private Long id;
    private UserDTO authorId;
    private Long postId;

    private String content;
    private LocalDateTime createdAt;

    private List<ReactionDTO> reactions;
}
