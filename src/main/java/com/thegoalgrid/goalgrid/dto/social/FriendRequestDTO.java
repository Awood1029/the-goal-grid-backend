package com.thegoalgrid.goalgrid.dto.social;

import com.thegoalgrid.goalgrid.dto.UserDTO;
import com.thegoalgrid.goalgrid.entity.FriendRequestStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FriendRequestDTO {
    private Long id;
    private UserDTO sender;
    private UserDTO recipient;
    private FriendRequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
