package com.nexxserve.inventoryservice.service.sync.out;

import com.nexxserve.inventoryservice.dto.sync.BrandSyncData;
import com.nexxserve.inventoryservice.repository.medicine.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BrandSyncService {

    private final BrandRepository brandRepository;

    /**
     * Find all variants (for full sync)
     */
    public Page<BrandSyncData> findAll(Pageable pageable) {
        return brandRepository.findAll(pageable).map(BrandSyncData::fromEntity);
    }

    /**
     * Find variants with sync version greater than specified value
     */
    public Page<BrandSyncData> findBySyncVersionGreaterThan(Double lastSyncVersion, Pageable pageable) {
        return brandRepository.findBySyncVersionGreaterThan(lastSyncVersion, pageable).map(BrandSyncData::fromEntity);
    }

}

