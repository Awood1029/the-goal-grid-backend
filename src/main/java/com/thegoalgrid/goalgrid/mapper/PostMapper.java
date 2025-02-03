package com.thegoalgrid.goalgrid.mapper;

import com.thegoalgrid.goalgrid.dto.UserDTO;
import com.thegoalgrid.goalgrid.dto.social.PostDTO;
import com.thegoalgrid.goalgrid.dto.social.ReactionDTO;
import com.thegoalgrid.goalgrid.dto.social.ReferencedGoalDTO;
import com.thegoalgrid.goalgrid.entity.Goal;
import com.thegoalgrid.goalgrid.entity.Post;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostMapper {

    private final ModelMapper modelMapper;
    private final ReactionMapper reactionMapper;

    public PostMapper(ModelMapper modelMapper, ReactionMapper reactionMapper) {
        this.modelMapper = modelMapper;
        this.reactionMapper = reactionMapper;
    }

    public PostDTO toDTO(Post post) {
        PostDTO postDTO = modelMapper.map(post, PostDTO.class);

        if (post.getAuthor() != null) {
            postDTO.setAuthor(modelMapper.map(post.getAuthor(), UserDTO.class));
        }

        // Map Goal to ReferencedGoalDTO
        if (post.getReferencedGoal() != null) {
            Goal goal = post.getReferencedGoal();
            ReferencedGoalDTO referencedGoalDTO = new ReferencedGoalDTO();
            referencedGoalDTO.setId(goal.getId());
            referencedGoalDTO.setReferencedGoalContent(goal.getDescription()); // Map 'description' to 'referencedGoalContent'
            postDTO.setReferencedGoal(referencedGoalDTO);
        }

        // Map reactions
        List<ReactionDTO> reactionDTOs = post.getPostReactions().stream()
                .map(reactionMapper::toDTO)
                .collect(Collectors.toList());
        postDTO.setReactions(reactionDTOs);

        // Set comment count
        int commentCount = post.getComments() != null ? post.getComments().size() : 0;
        postDTO.setCommentCount(commentCount);

        return postDTO;
    }

    public Post toEntity(PostDTO postDTO) {
        Post post = modelMapper.map(postDTO, Post.class);

        // Map ReferencedGoalDTO back to Goal entity
        if (postDTO.getReferencedGoal() != null) {
            Goal goal = new Goal();
            goal.setId(postDTO.getReferencedGoal().getId());
            goal.setDescription(postDTO.getReferencedGoal().getReferencedGoalContent()); // Preserve description
            post.setReferencedGoal(goal);
        }

        return post;
    }
}
