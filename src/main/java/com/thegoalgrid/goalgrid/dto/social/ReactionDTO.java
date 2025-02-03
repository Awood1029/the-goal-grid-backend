package com.thegoalgrid.goalgrid.dto.social;

import com.thegoalgrid.goalgrid.dto.UserDTO;
import com.thegoalgrid.goalgrid.entity.ReactionType;
import lombok.Data;

@Data
public class ReactionDTO {
    private Long id;
    private ReactionType type;
    private UserDTO user;
}
