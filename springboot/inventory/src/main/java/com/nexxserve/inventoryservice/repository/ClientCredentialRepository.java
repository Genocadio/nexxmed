package com.nexxserve.inventoryservice.repository;

import com.nexxserve.inventoryservice.entity.ClientCredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientCredentialRepository extends JpaRepository<ClientCredentialEntity, String> {
}