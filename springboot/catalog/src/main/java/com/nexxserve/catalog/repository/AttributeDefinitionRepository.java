package com.nexxserve.catalog.repository;

import com.nexxserve.catalog.model.entity.AttributeDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttributeDefinitionRepository extends JpaRepository<AttributeDefinition, UUID> {

    Optional<AttributeDefinition> findByKey(String key);

    List<AttributeDefinition> findByIsSearchableTrue();

    List<AttributeDefinition> findByIsFilterableTrue();

    List<AttributeDefinition> findAllByOrderByDisplayOrder();
}