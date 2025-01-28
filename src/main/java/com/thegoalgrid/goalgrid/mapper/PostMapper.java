package com.thegoalgrid.goalgrid.mapper;

import com.thegoalgrid.goalgrid.dto.UserDTO;
import com.thegoalgrid.goalgrid.dto.social.PostDTO;
import com.thegoalgrid.goalgrid.entity.Post;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    private final ModelMapper modelMapper;

    public PostMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public PostDTO toDTO(Post post) {
        PostDTO postDTO = modelMapper.map(post, PostDTO.class);
        // Ensure the post author is converted to UserDTO manually or via ModelMapper configuration
        if(post.getAuthor() != null) {
            postDTO.setAuthor(modelMapper.map(post.getAuthor(), UserDTO.class));
        }
        if(post.getReferencedGoal() != null) {
            postDTO.setReferencedGoalId(post.getReferencedGoal().getId());
        }
        return postDTO;
    }

    public Post toEntity(PostDTO postDTO) {
        return modelMapper.map(postDTO, Post.class);
    }
}
