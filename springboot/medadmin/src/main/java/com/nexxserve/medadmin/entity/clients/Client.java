package com.nexxserve.medadmin.entity.clients;

import com.nexxserve.medadmin.entity.BaseEntity;
import com.nexxserve.medadmin.enums.ClientStatus;
import com.nexxserve.medadmin.enums.ServiceType;
import com.nexxserve.medadmin.enums.SubscriptionType;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;
import com.nexxserve.medadmin.entity.Owners;
import lombok.Getter;
import lombok.Setter;
import com.nexxserve.medadmin.entity.Insurance;
import java.util.List;

@Entity
@Table(
        name = "clients",
        uniqueConstraints = @UniqueConstraint(columnNames = {"clientId", "owner_id"})
)
@Getter
@Setter
public class Client extends BaseEntity {

    @Column( nullable = false)
    private String clientId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    private String email;

    @Column(nullable = false)
    private String baseUrl;

    private String location;

    @Column(nullable = false)
    private ServiceType serviceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owners owner;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SubscriptionType subscriptionType;

    private Instant activationTime;

    @ManyToMany
    @JoinTable(
        name = "client_insurances",
        joinColumns = @JoinColumn(name = "client_id"),
        inverseJoinColumns = @JoinColumn(name = "insurance_id")
    )
    private List<Insurance> insurances;

    @Enumerated(EnumType.STRING)
    private ClientStatus status = ClientStatus.PENDING;

    private LocalDateTime lastTokenRefresh;
    private LocalDateTime lastHealthCheck;

}