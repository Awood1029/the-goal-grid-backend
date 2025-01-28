package com.thegoalgrid.goalgrid.repository;

import com.thegoalgrid.goalgrid.entity.CommentReaction;
import com.thegoalgrid.goalgrid.entity.ReactionType;
import com.thegoalgrid.goalgrid.entity.Comment;
import com.thegoalgrid.goalgrid.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long> {
    Optional<CommentReaction> findByUserAndTypeAndComment(User user, ReactionType type, Comment comment);
}
