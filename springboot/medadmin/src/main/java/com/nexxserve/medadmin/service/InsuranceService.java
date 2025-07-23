package com.nexxserve.medadmin.service;

import com.nexxserve.medadmin.dto.Insurance.InsuranceRequestDto;
import com.nexxserve.medadmin.entity.Insurance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    Page<Insurance> findAll(Pageable pageable);
    Page<Insurance> findBySyncVersionGreaterThan(Double version, Pageable pageable);
    List<Insurance> search(String query);
    List<Insurance> findBySyncVersionGreaterThan(Double version);
}