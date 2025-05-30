package com.nexxserve.billing.repository;

import com.nexxserve.billing.model.Consumable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ConsumableRepository extends JpaRepository<Consumable, Long> {
    List<Consumable> findByNameContainingIgnoreCase(String name);
    List<Consumable> findByIsGlobalTrue();
    List<Consumable> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
}