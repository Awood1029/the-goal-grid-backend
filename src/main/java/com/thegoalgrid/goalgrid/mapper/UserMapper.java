// File: main/java/com/thegoalgrid/goalgrid/mapper/UserMapper.java
package com.thegoalgrid.goalgrid.mapper;

import com.thegoalgrid.goalgrid.dto.UserDTO;
import com.thegoalgrid.goalgrid.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final ModelMapper modelMapper;

    public UserMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    public UserDTO toDTO(User user){
        return modelMapper.map(user, UserDTO.class);
    }

    public User toEntity(UserDTO userDTO){
        return modelMapper.map(userDTO, User.class);
    }
}
