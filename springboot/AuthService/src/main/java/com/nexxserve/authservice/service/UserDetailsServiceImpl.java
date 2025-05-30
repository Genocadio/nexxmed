package com.nexxserve.authservice.service;

import com.nexxserve.authservice.model.ServicePermission;
import com.nexxserve.authservice.model.ServiceRole;
import com.nexxserve.authservice.model.UserCredential;
import com.nexxserve.authservice.repository.UserCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserCredentialRepository userCredentialRepository;

    @Autowired
    public UserDetailsServiceImpl(UserCredentialRepository userCredentialRepository) {
        this.userCredentialRepository = userCredentialRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        UserCredential userCredential = userCredentialRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // Process service roles and their permissions
        Set<ServiceRole> serviceRoles = userCredential.getServiceRoles();
        for (ServiceRole role : serviceRoles) {
            // Add role with ROLE_ prefix
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

            // Add permissions from this role
            for (ServicePermission permission : role.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(permission.getPermission()));
            }
        }

        return new User(userCredential.getUserId(), userCredential.getPassword(), authorities);
    }
}