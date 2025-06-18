package com.nexxserve.medicine.repository;

import com.nexxserve.medicine.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VariantRepository extends JpaRepository<Variant, UUID> {

    @Query("SELECT DISTINCT v FROM Variant v LEFT JOIN FETCH v.generics WHERE LOWER(v.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Variant> findByNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT DISTINCT v FROM Variant v JOIN v.generics g WHERE g.id = :genericId")
    List<Variant> findByGenericId(@Param("genericId") UUID genericId);

    @Query("SELECT DISTINCT v FROM Variant v WHERE LOWER(v.form) LIKE LOWER(CONCAT('%', :form, '%'))")
    List<Variant> findByFormContainingIgnoreCase(@Param("form") String form);

    @Query("SELECT DISTINCT v FROM Variant v WHERE LOWER(v.route) LIKE LOWER(CONCAT('%', :route, '%'))")
    List<Variant> findByRouteContainingIgnoreCase(@Param("route") String route);

    @Query("SELECT DISTINCT v FROM Variant v WHERE LOWER(v.tradeName) LIKE LOWER(CONCAT('%', :tradeName, '%'))")
    List<Variant> findByTradeNameContainingIgnoreCase(@Param("tradeName") String tradeName);
}