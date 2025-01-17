package com.thegoalgrid.goalgrid.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "user_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String uniqueUrl;

    @Column(unique = true, nullable = false)
    private String inviteCode;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "group_users",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private Set<User> users = new HashSet<>();

    @PrePersist
    public void generateUniqueFields() {
        this.uniqueUrl = UUID.randomUUID().toString();
        this.inviteCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Helper methods to manage bidirectional relationship
    public void addUser(User user) {
        users.add(user);
        user.getGroups().add(this);
    }
}
