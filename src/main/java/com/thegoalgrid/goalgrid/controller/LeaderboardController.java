// File: main/java/com/thegoalgrid/goalgrid/controller/LeaderboardController.java
package com.thegoalgrid.goalgrid.controller;

import com.thegoalgrid.goalgrid.dto.GroupDTO;
import com.thegoalgrid.goalgrid.entity.Group;
import com.thegoalgrid.goalgrid.mapper.GroupMapper;
import com.thegoalgrid.goalgrid.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;
    private final GroupMapper groupMapper;
    private static final Logger logger = LoggerFactory.getLogger(LeaderboardController.class);

    /**
     * Retrieve the leaderboard sorted by boards completed.
     *
     * @return List of GroupDTOs sorted by leaderboard ranking.
     */
    @GetMapping
    public ResponseEntity<List<GroupDTO>> getLeaderboard() {
        try {
            logger.info("Fetching leaderboard.");
            List<Group> leaderboard = leaderboardService.getLeaderboard();
            List<GroupDTO> groupDTOs = leaderboard.stream()
                    .map(groupMapper::toDTO)
                    .collect(Collectors.toList());
            logger.info("Fetched leaderboard with {} groups.", groupDTOs.size());
            return ResponseEntity.ok(groupDTOs);
        } catch (Exception e) {
            logger.error("Error fetching leaderboard: {}", e.getMessage());
            throw e; // Let global exception handler manage it
        }
    }
}
