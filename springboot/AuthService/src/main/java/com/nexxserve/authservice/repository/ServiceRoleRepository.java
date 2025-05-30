package com.nexxserve.authservice.repository;

import com.nexxserve.authservice.model.ServicePermission;
import com.nexxserve.authservice.model.ServiceRole;
import com.nexxserve.authservice.model.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRoleRepository extends JpaRepository<ServiceRole, Long> {
    Optional<ServiceRole> findByNameAndService(String name, Services service);
    List<ServiceRole> findByService(Services service);
    List<ServiceRole> findByName(String name);
    @Query("SELECT r FROM ServiceRole r LEFT JOIN FETCH r.permissions WHERE r.service.id = :serviceId AND r.name = 'USER'")
    Optional<ServiceRole> findUserRoleWithPermissions(@Param("serviceId") Long serviceId);
    @Query("SELECT p FROM ServiceRole r JOIN r.permissions p WHERE r.id = :roleId")
    List<ServicePermission> findPermissionsByRole(@Param("roleId") Long roleId);

    @Query("SELECT DISTINCT sr FROM ServiceRole sr " +
            "LEFT JOIN FETCH sr.permissions p " +
            "WHERE sr.name = :name AND sr.service = :service")
    Optional<ServiceRole> findByNameAndServiceWithPermissions(@Param("name") String name, @Param("service") Services service);


}