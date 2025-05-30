package com.nexxserve.billing.repository;

 import com.nexxserve.billing.model.ServiceActivity;
 import org.springframework.data.jpa.repository.JpaRepository;
 import org.springframework.data.jpa.repository.Query;
 import org.springframework.data.repository.query.Param;

 import java.math.BigDecimal;
 import java.util.List;

 public interface ServiceActivityRepository extends JpaRepository<ServiceActivity, Long> {
     List<ServiceActivity> findByServiceId(Long serviceId);
     List<ServiceActivity> findByNameContainingIgnoreCase(String name);
     List<ServiceActivity> findByPriceLessThanEqual(BigDecimal maxPrice);

    @Query("SELECT sa FROM ServiceActivity sa JOIN sa.activityConsumables ac WHERE ac.consumable.id = :consumableId")
    List<ServiceActivity> findByConsumableId(@Param("consumableId") Long consumableId);
 }