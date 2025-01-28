package com.thegoalgrid.goalgrid.repository;

import com.thegoalgrid.goalgrid.entity.PostReaction;
import com.thegoalgrid.goalgrid.entity.ReactionType;
import com.thegoalgrid.goalgrid.entity.Post;
import com.thegoalgrid.goalgrid.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {
    Optional<PostReaction> findByUserAndTypeAndPost(User user, ReactionType type, Post post);
}
