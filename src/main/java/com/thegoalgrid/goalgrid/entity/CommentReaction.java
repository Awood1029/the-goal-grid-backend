package com.thegoalgrid.goalgrid.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
        name = "comment_reactions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "type", "comment_id"})
        }
)
@Data
public class CommentReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;
}
