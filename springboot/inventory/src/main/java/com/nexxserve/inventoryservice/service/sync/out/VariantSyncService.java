package com.nexxserve.inventoryservice.service.sync.out;

import com.nexxserve.inventoryservice.dto.sync.VariantSyncData;
import com.nexxserve.inventoryservice.repository.medicine.VariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VariantSyncService {

    private final VariantRepository variantRepository;

    /**
     * Find all variants (for full sync)
     */
    public Page<VariantSyncData> findAll(Pageable pageable) {
        return variantRepository.findAll(pageable).map(VariantSyncData::fromEntity);
    }

    /**
     * Find variants with sync version greater than specified value
     */
    public Page<VariantSyncData> findBySyncVersionGreaterThan(Double lastSyncVersion, Pageable pageable) {
        return variantRepository.findBySyncVersionGreaterThan(lastSyncVersion, pageable).map(VariantSyncData::fromEntity);
    }


}