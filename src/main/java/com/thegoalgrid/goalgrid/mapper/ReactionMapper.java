package com.thegoalgrid.goalgrid.mapper;

import com.thegoalgrid.goalgrid.dto.social.ReactionDTO;
import com.thegoalgrid.goalgrid.entity.CommentReaction;
import com.thegoalgrid.goalgrid.entity.PostReaction;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ReactionMapper {

    private final ModelMapper modelMapper;

    public ReactionMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    // Mapping for PostReaction
    public ReactionDTO toDTO(PostReaction reaction) {
        return modelMapper.map(reaction, ReactionDTO.class);
    }

    public PostReaction toPostReactionEntity(ReactionDTO reactionDTO) {
        return modelMapper.map(reactionDTO, PostReaction.class);
    }

    // Mapping for CommentReaction
    public ReactionDTO toDTO(CommentReaction reaction) {
        return modelMapper.map(reaction, ReactionDTO.class);
    }

    public CommentReaction toCommentReactionEntity(ReactionDTO reactionDTO) {
        return modelMapper.map(reactionDTO, CommentReaction.class);
    }
}

