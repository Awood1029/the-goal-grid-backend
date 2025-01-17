// File: main/java/com/thegoalgrid/goalgrid/mapper/GroupMapper.java
package com.thegoalgrid.goalgrid.mapper;

import com.thegoalgrid.goalgrid.dto.GroupDTO;
import com.thegoalgrid.goalgrid.entity.Group;
import com.thegoalgrid.goalgrid.mapper.BoardMapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class GroupMapper {

    private final ModelMapper modelMapper;
    private final BoardMapper boardMapper;

    public GroupMapper(ModelMapper modelMapper, BoardMapper boardMapper) {
        this.modelMapper = modelMapper;
        this.boardMapper = boardMapper;
    }

    public GroupDTO toDTO(Group group) {
        GroupDTO dto = modelMapper.map(group, GroupDTO.class);
        // Populate the boards field by mapping each user's personal board
        dto.setBoards(
                group.getUsers().stream()
                        .map(user -> boardMapper.toDTO(user.getBoard()))
                        .collect(Collectors.toSet())
        );
        return dto;
    }

    public Group toEntity(GroupDTO groupDTO) {
        return modelMapper.map(groupDTO, Group.class);
    }
}
