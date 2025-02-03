package com.thegoalgrid.goalgrid.mapper;

import com.thegoalgrid.goalgrid.dto.social.FriendRequestDTO;
import com.thegoalgrid.goalgrid.entity.FriendRequest;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class FriendRequestMapper {

    private final ModelMapper modelMapper;

    public FriendRequestMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public FriendRequestDTO toDTO(FriendRequest friendRequest) {
        return modelMapper.map(friendRequest, FriendRequestDTO.class);
    }

    public FriendRequest toEntity(FriendRequestDTO friendRequestDTO) {
        return modelMapper.map(friendRequestDTO, FriendRequest.class);
    }
}
