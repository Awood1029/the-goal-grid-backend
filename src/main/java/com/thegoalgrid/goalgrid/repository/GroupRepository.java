package com.thegoalgrid.goalgrid.repository;

import com.thegoalgrid.goalgrid.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByUniqueUrl(String uniqueUrl);
    Optional<Group> findByInviteCode(String inviteCode);
}
