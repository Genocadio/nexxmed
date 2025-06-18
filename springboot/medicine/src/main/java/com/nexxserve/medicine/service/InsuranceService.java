package com.nexxserve.medicine.service;

import com.nexxserve.medicine.dto.InsuranceRequestDto;
import com.nexxserve.medicine.entity.Insurance;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InsuranceService {
    Insurance createInsurance(InsuranceRequestDto requestDto, String username);
    Insurance updateInsurance(UUID id, InsuranceRequestDto requestDto, String username);
    void deleteInsurance(UUID id);
    Optional<Insurance> findById(UUID id);
    Optional<Insurance> findByName(String name);
    List<Insurance> findByActive(Boolean active);
    List<Insurance> findAll();
    List<Insurance> search(String query);
}