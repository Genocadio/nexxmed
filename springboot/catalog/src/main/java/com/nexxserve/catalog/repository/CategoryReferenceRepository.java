package com.nexxserve.catalog.repository;

import com.nexxserve.catalog.model.entity.CategoryReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryReferenceRepository extends JpaRepository<CategoryReference, UUID> {

    Optional<CategoryReference> findByCode(String code);

    List<CategoryReference> findByParentId(UUID parentId);

    List<CategoryReference> findByLevelOrderByDisplayOrder(Integer level);

    List<CategoryReference> findByIsActiveTrueOrderByDisplayOrder();

    @Query("SELECT c FROM CategoryReference c WHERE c.parent IS NULL ORDER BY c.displayOrder")
    List<CategoryReference> findRootCategories();

    @Query(value = "SELECT CASE WHEN COUNT(pf) > 0 THEN true ELSE false END FROM product_families pf WHERE pf.category_id = :categoryId", nativeQuery = true)
    boolean hasProductFamilyReferences(@Param("categoryId") UUID categoryId);
}