package com.nexxserve.medadmin.service;

import com.nexxserve.medadmin.dto.request.OwnerRegistrationRequestDto;
import com.nexxserve.medadmin.dto.response.OwnerRegistrationResponseDto;
import com.nexxserve.medadmin.entity.Owners;
import com.nexxserve.medadmin.repository.OwnersRepository;
import com.nexxserve.medadmin.repository.clients.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnersService {
    private final OwnersRepository ownersRepository;
    private final ClientRepository clientRepository;

    public OwnerRegistrationResponseDto registerOwner(OwnerRegistrationRequestDto owner) {

        Owners saved = ownersRepository.save(owner.toEntity());
        int organisationCount = clientRepository.countByOwnerId(saved.getId());
        return OwnerRegistrationResponseDto.fromEntity(saved, organisationCount);
    }

    public List<OwnerRegistrationResponseDto> getAllOwners() {
        return ownersRepository.findAll().stream()
                .map(owner -> {
                    int organisationCount = clientRepository.countByOwnerId(owner.getId());
                    return OwnerRegistrationResponseDto.fromEntity(owner, organisationCount);
                })
                .toList();
    }
}