package com.nexxserve.inventoryservice.service.sync.out;

import com.nexxserve.inventoryservice.dto.sync.ProductVariantSyncData;
import com.nexxserve.inventoryservice.repository.catalog.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductVariantSyncService {

    private final ProductVariantRepository productVariantRepository;

    public Page<ProductVariantSyncData> findBySyncVersionGreaterThan(Double lastSyncVersion, Pageable pageable) {
        return productVariantRepository.findBySyncVersionGreaterThan(lastSyncVersion, pageable)
                .map(ProductVariantSyncData::fromEntity);
    }

    public Page<ProductVariantSyncData> findAll(Pageable pageable) {
        return productVariantRepository.findAll(pageable).map(ProductVariantSyncData::fromEntity);
    }
}

