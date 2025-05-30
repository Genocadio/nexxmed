package com.nexxserve.users.repository;

import com.nexxserve.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(String userId);
    // Add these methods to UserRepository.java
    Optional<User> findByPhone(String phone);
    Optional<User> findByUsername(String username);// Add these methods to UserRepository.java

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByUserIdAndClinicId(String userId, String clinicId);
}