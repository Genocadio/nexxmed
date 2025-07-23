package com.nexxserve.medadmin.dto.response;

import com.nexxserve.medadmin.entity.Owners;
import com.nexxserve.medadmin.enums.ServiceType;
import com.nexxserve.medadmin.enums.SubscriptionType;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class OwnerRegistrationResponseDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private SubscriptionType subscriptionType;
    private Set<ServiceType> services;
    private int organisationCount;
    public static OwnerRegistrationResponseDto fromEntity(Owners owner, int organisationCount) {
        OwnerRegistrationResponseDto dto = new OwnerRegistrationResponseDto();
        dto.setId(owner.getId());
        dto.setFirstName(owner.getFirstName());
        dto.setLastName(owner.getLastName());
        dto.setEmail(owner.getEmail());
        dto.setPhoneNumber(owner.getPhoneNumber());
        dto.setSubscriptionType(owner.getSubscriptionType());
        dto.setServices(owner.getServices());
        dto.setOrganisationCount(organisationCount);
        return dto;
    }
}