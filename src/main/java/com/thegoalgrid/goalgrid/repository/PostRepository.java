package com.thegoalgrid.goalgrid.repository;

import com.thegoalgrid.goalgrid.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    // For main feed: posts by a list of author IDs
    @EntityGraph(attributePaths = {"postReactions", "postReactions.user"})
    Page<Post> findByAuthor_IdIn(List<Long> authorIds, Pageable pageable);

    // For goal feed: posts referencing a specific goal
    @EntityGraph(attributePaths = {"postReactions", "postReactions.user"})
    Page<Post> findByReferencedGoal_Id(Long goalId, Pageable pageable);

    // For group feed: posts by group member IDs
    @EntityGraph(attributePaths = {"postReactions", "postReactions.user"})
    Page<Post> findByAuthor_IdInAndReferencedGoal_IdIsNull(List<Long> authorIds, Pageable pageable);

    // NEW: Retrieve posts for a specific author.
    // This method was missing, so we add it to enable getRecentPostsByUser in PostService.
    @EntityGraph(attributePaths = {"postReactions", "postReactions.user"})
    Page<Post> findByAuthor_Id(Long authorId, Pageable pageable);
}
