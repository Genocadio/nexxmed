package com.nexxserve.catalog.repository;

import com.nexxserve.catalog.enums.ProductStatus;
import com.nexxserve.catalog.model.entity.ProductFamily;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductFamilyRepository extends JpaRepository<ProductFamily, UUID> {

    Page<ProductFamily> findByStatus(ProductStatus status, Pageable pageable);

    List<ProductFamily> findByBrand(String brand);

    Page<ProductFamily> findByCategoryId(UUID categoryId, Pageable pageable);

    @Query("SELECT pf FROM ProductFamily pf WHERE " +
            "LOWER(pf.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(pf.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(pf.searchKeywords) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<ProductFamily> searchByTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT pf FROM ProductFamily pf JOIN pf.tags t WHERE t IN :tags")
    List<ProductFamily> findByTagsIn(@Param("tags") List<String> tags);


}