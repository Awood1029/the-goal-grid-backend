package com.thegoalgrid.goalgrid.mapper;

import com.thegoalgrid.goalgrid.dto.social.CommentDTO;
import com.thegoalgrid.goalgrid.entity.Comment;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    private final ModelMapper modelMapper;

    public CommentMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public CommentDTO toDTO(Comment comment) {
        CommentDTO commentDTO = modelMapper.map(comment, CommentDTO.class);
        if(comment.getAuthor() != null) {
            commentDTO.setAuthorId(modelMapper.map(comment.getAuthor(), com.thegoalgrid.goalgrid.dto.UserDTO.class));
        }
        if(comment.getPost() != null) {
            commentDTO.setPostId(comment.getPost().getId());
        }
        return commentDTO;
    }

    public Comment toEntity(CommentDTO commentDTO) {
        return modelMapper.map(commentDTO, Comment.class);
    }
}
