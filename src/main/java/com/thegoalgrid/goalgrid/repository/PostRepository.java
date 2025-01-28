package com.thegoalgrid.goalgrid.repository;

import com.thegoalgrid.goalgrid.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // Used to retrieve posts (e.g., for feeds)
    List<Post> findByAuthor_IdIn(List<Long> authorIds);

    // Used for goal feed: only posts that reference the given goal
    List<Post> findByReferencedGoal_Id(Long goalId);

    // New method to find post by ID and author ID
    Optional<Post> findByIdAndAuthor_Id(Long id, Long authorId);
}
