package com.nexxserve.authservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_credentials")
public class UserCredential {
    @Id
    private String userId;
    private String password;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_service_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "service_role_id")
    )
    @Builder.Default
    private Set<ServiceRole> serviceRoles = new HashSet<>();

    // Helper method to safely add roles
    public void addServiceRole(ServiceRole role) {
        if (this.serviceRoles == null) {
            this.serviceRoles = new HashSet<>();
        }
        this.serviceRoles.add(role);
    }

    // Helper method to safely remove roles
    public void removeServiceRole(ServiceRole role) {
        if (this.serviceRoles != null) {
            this.serviceRoles.remove(role);
        }
    }
}