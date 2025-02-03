package com.thegoalgrid.goalgrid.repository;

import com.thegoalgrid.goalgrid.entity.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Find all comments by post ID with sorting.
     */
    List<Comment> findByPost_Id(Long postId, Sort sort);

    /**
     * Find all comments by goal ID with sorting (if needed).
     */
    List<Comment> findByGoal_Id(Long goalId, Sort sort);
}
