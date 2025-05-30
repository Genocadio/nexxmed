package com.nexxserve.authservice.repository;

import com.nexxserve.authservice.model.ServicePermission;
import com.nexxserve.authservice.model.ServiceRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicePermissionRepository extends JpaRepository<ServicePermission, Long> {
    List<ServicePermission> findByRoleService_Name(String serviceName);
    List<ServicePermission> findByRoleNameAndRoleService_Name(String roleName, String serviceName);
    boolean existsByPermission(String permission);
    Optional<ServicePermission> findByPermissionAndRole(String permission, ServiceRole role);
    boolean existsByPermissionAndRoleName(String permission, String roleName);
    List<ServicePermission> findByRoleId(Long roleId);
}