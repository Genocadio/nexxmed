package com.nexxserve.medadmin.dto.response;

import com.nexxserve.medadmin.entity.clients.Client;
import com.nexxserve.medadmin.enums.ClientStatus;
import com.nexxserve.medadmin.enums.ServiceType;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import com.nexxserve.medadmin.dto.Insurance.InsuranceResponseDto;

@Getter
@Setter
public class CreateClientResponse {
    private String id;
    private String clientId;
    private String password;
    private String name;
    private String phoneNumber;
    private String email;
    private String location;
    private ServiceType serviceType;
    private ClientStatus status;
    private OwnerResponseDto owner;
    private List<InsuranceResponseDto> insurances;

    public static CreateClientResponse fromEntity(Client client) {
        CreateClientResponse response = new CreateClientResponse();
        response.setId(client.getId().toString());
        response.setClientId(client.getClientId());
        response.setPassword(client.getPassword());
        response.setName(client.getName());
        response.setPhoneNumber(client.getPhone());
        response.setEmail(client.getEmail());
        response.setLocation(client.getLocation());
        response.setServiceType(client.getServiceType());
        response.setStatus(client.getStatus());
        response.setOwner(OwnerResponseDto.fromEntity(client.getOwner()));
        if (client.getInsurances() != null) {
            response.setInsurances(
                client.getInsurances().stream()
                    .map(InsuranceResponseDto::fromEntity)
                    .toList()
            );
        }
        return response;
    }
}
