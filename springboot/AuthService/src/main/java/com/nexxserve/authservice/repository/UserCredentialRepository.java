package com.nexxserve.authservice.repository;

import com.nexxserve.authservice.model.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, String> {

    /**
     * Find user credentials by userId
     *
     * @param userId the user's unique identifier
     * @return the user credentials or empty if not found
     */
    Optional<UserCredential> findByUserId(String userId);

    /**
     * Check if user credentials exist by userId
     *
     * @param userId the user's unique identifier
     * @return true if credentials exist, false otherwise
     */
    boolean existsByUserId(String userId);


    // Method to fetch user with all related data in one query
    @Query("SELECT DISTINCT uc FROM UserCredential uc " +
            "LEFT JOIN FETCH uc.serviceRoles sr " +
            "LEFT JOIN FETCH sr.service s " +
            "LEFT JOIN FETCH sr.permissions p " +
            "WHERE uc.userId = :userId")
    Optional<UserCredential> findByUserIdWithRolesAndPermissions(@Param("userId") String userId);
}