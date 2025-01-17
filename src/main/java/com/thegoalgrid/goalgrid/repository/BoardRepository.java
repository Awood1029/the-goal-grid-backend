package com.thegoalgrid.goalgrid.repository;

import com.thegoalgrid.goalgrid.entity.Board;
import com.thegoalgrid.goalgrid.entity.Group;
import com.thegoalgrid.goalgrid.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    Board findByOwner(User owner);
    Optional<Board> findByOwnerAndGroupsContaining(User owner, Group group);
    Optional<Board> findByIdAndOwner(Long boardId, User owner);
}