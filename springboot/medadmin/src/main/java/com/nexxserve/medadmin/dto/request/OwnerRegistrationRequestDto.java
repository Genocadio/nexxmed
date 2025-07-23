package com.nexxserve.medadmin.dto.request;

import com.nexxserve.medadmin.entity.Owners;
import com.nexxserve.medadmin.enums.ServiceType;
import com.nexxserve.medadmin.enums.SubscriptionType;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class OwnerRegistrationRequestDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private SubscriptionType subscriptionType;
    private Set<ServiceType> services;
    public Owners toEntity() {
        Owners owner = new Owners();
        owner.setFirstName(this.firstName);
        owner.setLastName(this.lastName);
        owner.setEmail(this.email);
        owner.setPhoneNumber(this.phoneNumber);
        owner.setSubscriptionType(this.subscriptionType);
        owner.setServices(this.services);
        return owner;
    }
}