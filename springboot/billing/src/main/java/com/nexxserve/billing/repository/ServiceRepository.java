package com.nexxserve.billing.repository;

import com.nexxserve.billing.model.ServiceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceRepository extends JpaRepository<ServiceModel, Long> {
    List<ServiceModel> findByNameContainingIgnoreCase(String name);

    @Query("SELECT s FROM ServiceModel s JOIN s.activityIds aid WHERE aid = :activityId")
    ServiceModel findByActivityId(@Param("activityId") Long activityId);
}