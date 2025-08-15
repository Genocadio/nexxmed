package com.nexxserve.medadmin.repository.clients;


import com.nexxserve.medadmin.entity.clients.Client;
import com.nexxserve.medadmin.enums.ClientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
    Optional<Client> findByClientId(String clientId);
    List<Client> findByStatus(ClientStatus status);

    int countByOwnerId(UUID ownerId);

    @Query("SELECT c FROM Client c WHERE c.status = 'ACTIVE' AND c.lastTokenRefresh < :threshold")
    List<Client> findActiveClientsWithExpiredTokens(LocalDateTime threshold);
    Optional<Client> findByNameAndPhone(String name, String phone);

    @Query("SELECT c FROM Client c WHERE c.status = 'ACTIVE' AND c.lastHealthCheck < :threshold")
    List<Client> findActiveClientsWithOldHealthCheck(LocalDateTime threshold);
}

