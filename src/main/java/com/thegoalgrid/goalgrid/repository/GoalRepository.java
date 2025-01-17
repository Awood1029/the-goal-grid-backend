package com.thegoalgrid.goalgrid.repository;

import com.thegoalgrid.goalgrid.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {
}
