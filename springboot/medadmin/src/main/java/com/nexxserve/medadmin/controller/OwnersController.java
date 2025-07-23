package com.nexxserve.medadmin.controller;

import com.nexxserve.medadmin.dto.request.OwnerRegistrationRequestDto;
import com.nexxserve.medadmin.dto.response.OwnerRegistrationResponseDto;
import com.nexxserve.medadmin.entity.Owners;
import com.nexxserve.medadmin.service.OwnersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class OwnersController {
    private final OwnersService ownersService;

    @PostMapping("/register")
    public OwnerRegistrationResponseDto registerOwner(@RequestBody OwnerRegistrationRequestDto owner) {
        return ownersService.registerOwner(owner);
    }

    @GetMapping
    public List<OwnerRegistrationResponseDto> getAllOwners() {
        return ownersService.getAllOwners();
    }
}