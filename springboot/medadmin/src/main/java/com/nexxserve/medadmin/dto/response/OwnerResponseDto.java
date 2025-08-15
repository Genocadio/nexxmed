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
public class OwnerResponseDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private SubscriptionType subscriptionType;
    private Set<ServiceType> services;
    public static OwnerResponseDto fromEntity(Owners owner) {
        OwnerResponseDto dto = new OwnerResponseDto();
        dto.setId(owner.getId());
        dto.setFirstName(owner.getFirstName());
        dto.setLastName(owner.getLastName());
        dto.setEmail(owner.getEmail());
        dto.setPhoneNumber(owner.getPhoneNumber());
        dto.setSubscriptionType(owner.getSubscriptionType());
        dto.setServices(owner.getServices());
        return dto;
    }
}
