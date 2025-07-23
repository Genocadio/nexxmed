package com.nexxserve.inventoryservice.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "client_credentials")
public class ClientCredentialEntity {
    // Getters and setters
    @Id
    private String id = "client_singleton"; // Single row for client app

    private String encryptedClientId;
    private String encryptedPassword;
    @Column(columnDefinition = "TEXT")
    private String encryptedToken;
    private boolean registered;
    private boolean activated;

}