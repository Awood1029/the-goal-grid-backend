// File: main/java/com/thegoalgrid/goalgrid/dto/GroupDTO.java
package com.thegoalgrid.goalgrid.dto;

import com.thegoalgrid.goalgrid.dto.board.BoardDTO;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class GroupDTO {
    private Long id;
    private String name;
    private String uniqueUrl;
    private String inviteCode;
    private Set<UserDTO> users;
    private Set<BoardDTO> boards;
    private Map<String, Integer> leaderboard;
}
