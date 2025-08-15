package com.nexxserve.medadmin.repository.admin;


import com.nexxserve.medadmin.entity.clients.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
