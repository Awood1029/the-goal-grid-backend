package com.thegoalgrid.goalgrid.mapper;

import com.thegoalgrid.goalgrid.dto.social.UserProfileDTO;
import com.thegoalgrid.goalgrid.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserProfileMapper {

    private final ModelMapper modelMapper;
    private final UserMapper userMapper;

    public UserProfileMapper(ModelMapper modelMapper, UserMapper userMapper) {
        this.modelMapper = modelMapper;
        this.userMapper = userMapper;
    }

    public UserProfileDTO toDTO(User user) {
        // Map basic fields automatically.
        UserProfileDTO profileDTO = modelMapper.map(user, UserProfileDTO.class);
        // Map friends collection manually using UserMapper
        if (user.getFriends() != null) {
            profileDTO.setFriends(
                    user.getFriends().stream()
                            .map(userMapper::toDTO)
                            .collect(Collectors.toSet())
            );
        }
        return profileDTO;
    }
}
