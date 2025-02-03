package com.thegoalgrid.goalgrid.mapper;

import com.thegoalgrid.goalgrid.dto.social.CommentDTO;
import com.thegoalgrid.goalgrid.dto.social.ReactionDTO;
import com.thegoalgrid.goalgrid.entity.Comment;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper {

    private final ModelMapper modelMapper;
    private final ReactionMapper reactionMapper; // Add this line

    public CommentMapper(ModelMapper modelMapper, ReactionMapper reactionMapper) { // Update constructor
        this.modelMapper = modelMapper;
        this.reactionMapper = reactionMapper; // Initialize the mapper
    }

    public CommentDTO toDTO(Comment comment) {
        CommentDTO commentDTO = modelMapper.map(comment, CommentDTO.class);
        if(comment.getAuthor() != null) {
            commentDTO.setAuthorId(modelMapper.map(comment.getAuthor(), com.thegoalgrid.goalgrid.dto.UserDTO.class));
        }
        if(comment.getPost() != null) {
            commentDTO.setPostId(comment.getPost().getId());
        }

        // Map reactions
        List<ReactionDTO> reactionDTOs = comment.getCommentReactions().stream()
                .map(reactionMapper::toDTO)
                .collect(Collectors.toList());
        commentDTO.setReactions(reactionDTOs);

        return commentDTO;
    }

    public Comment toEntity(CommentDTO commentDTO) {
        return modelMapper.map(commentDTO, Comment.class);
    }
}
