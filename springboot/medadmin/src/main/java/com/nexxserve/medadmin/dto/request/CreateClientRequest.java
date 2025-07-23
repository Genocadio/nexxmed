package com.nexxserve.medadmin.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;
import com.nexxserve.medadmin.enums.ServiceType;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateClientRequest {
    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "Service type is required")
    private ServiceType serviceType;

    @NotNull(message = "Location is required")
    private String location;

    @NotNull(message = "Owner ID is required")
    private UUID ownerId;

    private List<UUID> insuranceIds;

    private String phoneNumber;
    private String email;
}
