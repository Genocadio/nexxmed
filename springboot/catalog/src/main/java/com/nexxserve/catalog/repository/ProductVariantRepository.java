package com.nexxserve.catalog.repository;

import com.nexxserve.catalog.enums.ProductStatus;
import com.nexxserve.catalog.model.entity.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {

    Optional<ProductVariant> findBySku(String sku);

    Optional<ProductVariant> findByUpc(String upc);

    Optional<ProductVariant> findByGtin(String gtin);

    List<ProductVariant> findByFamilyId(UUID familyId);

    Page<ProductVariant> findByStatus(ProductStatus status, Pageable pageable);

    List<ProductVariant> findByBrand(String brand);

    @Query("SELECT pv FROM ProductVariant pv WHERE " +
            "LOWER(pv.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(pv.searchKeywords) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<ProductVariant> searchByTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT pv FROM ProductVariant pv WHERE pv.sku IN :barcodes OR pv.upc IN :barcodes OR pv.gtin IN :barcodes")
    List<ProductVariant> findByBarcodes(@Param("barcodes") List<String> barcodes);
}
