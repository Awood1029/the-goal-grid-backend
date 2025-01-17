// BoardDTO.java
package com.thegoalgrid.goalgrid.dto.board;

import com.thegoalgrid.goalgrid.dto.GoalDTO;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class BoardDTO {
    private Long id;
    private Long ownerId;
    private String name;
    private List<GoalDTO> goals;
    private Integer completedRows;
    private Integer completedDiagonals;
}
