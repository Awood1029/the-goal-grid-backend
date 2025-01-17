package com.thegoalgrid.goalgrid.dto.board;

import lombok.Data;

@Data
public class GoalUpdateDTO {
    private Long goalId;
    private String description;
    private Boolean completed;
}
