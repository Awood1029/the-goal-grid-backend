package com.thegoalgrid.goalgrid.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
        name = "post_reactions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "type", "post_id"})
        }
)
@Data
public class PostReaction {

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
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}
