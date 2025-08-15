package com.nexxserve.medadmin.repository;

import com.nexxserve.medadmin.entity.Admins;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admins, Long> {
    Optional<Admins> findByUsername(String username);
}