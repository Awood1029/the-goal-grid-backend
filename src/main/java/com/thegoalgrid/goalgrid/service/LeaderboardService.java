package com.thegoalgrid.goalgrid.service;

import com.thegoalgrid.goalgrid.entity.Group;
import com.thegoalgrid.goalgrid.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final GroupRepository groupRepository;
    private final GroupService groupService;

    /**
     * Retrieve the leaderboard sorted by boards completed (rows + diagonals).
     *
     * @return List of Groups sorted by leaderboard ranking.
     */
    public List<Group> getLeaderboard() {
        List<Group> groups = groupRepository.findAll();
        return groups.stream()
                .sorted(Comparator.comparingInt(this::getBoardsCompleted).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Calculate the total completed rows and diagonals for a group.
     *
     * @param group Group entity.
     * @return Total completed rows and diagonals.
     */
    private int getBoardsCompleted(@Nonnull Group group) {
        return  groupService.getBoards(group.getUniqueUrl()).stream()
                .mapToInt(board -> board.getCompletedRows() + board.getCompletedDiagonals())
                .sum();
    }
}
