package com.nexxserve.medadmin.repository;

import com.nexxserve.medadmin.entity.Owners;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OwnersRepository extends JpaRepository<Owners, UUID> {

}
