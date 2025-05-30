package com.nexxserve.billing.repository;

import com.nexxserve.billing.model.ActivityConsumable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityConsumableRepository extends JpaRepository<ActivityConsumable, Long> {
    void deleteByActivityIdAndConsumableId(Long activityId, Long consumableId);
}