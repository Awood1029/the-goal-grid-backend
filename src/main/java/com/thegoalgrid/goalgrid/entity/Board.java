package com.thegoalgrid.goalgrid.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "boards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"groups", "goals", "owner"})
@EqualsAndHashCode(exclude = {"groups", "goals", "owner"})
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String name;

    @ManyToMany
    @JoinTable(
            name = "board_groups",
            joinColumns = @JoinColumn(name = "board_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    @JsonIgnore
    private Set<Group> groups = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIgnore
    private User owner;

    @OneToMany(
            mappedBy = "board",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private Set<Goal> goals = new HashSet<>();

    @Column(nullable = false)
    private Integer completedRows = 0;

    @Column(nullable = false)
    private Integer completedDiagonals = 0;

    // Helper method to manage bidirectional relationship
    public void addGoal(Goal goal) {
        goals.add(goal);
        goal.setBoard(this);
    }

    public void removeGoal(Goal goal) {
        goals.remove(goal);
        goal.setBoard(null);
    }
}