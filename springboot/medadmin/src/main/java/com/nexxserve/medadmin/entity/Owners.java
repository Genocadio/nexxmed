package com.nexxserve.medadmin.entity;

import jakarta.persistence.*;
import com.nexxserve.medadmin.enums.ServiceType;
import com.nexxserve.medadmin.enums.SubscriptionType;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;


@Entity
@Getter
@Setter
public class Owners extends BaseEntity{
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private SubscriptionType subscriptionType; // YEARLY or MONTHLY

    @ElementCollection(targetClass = ServiceType.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "client_services", joinColumns = @JoinColumn(name = "client_id"))
    @Column(name = "service")
    private Set<ServiceType> services;
}
