// GoalDTO.java
package com.thegoalgrid.goalgrid.dto;

import lombok.Data;

@Data
public class GoalDTO {
    private Long id;
    private String description;
    private Integer position;
    private Long boardId;
    private Boolean completed;
}
