package com.nexxserve.authservice.repository;

import com.nexxserve.authservice.model.Services;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceRepository extends JpaRepository<Services, Long> {
    Optional<Services> findByName(String name);
    boolean existsByName(String name);
}