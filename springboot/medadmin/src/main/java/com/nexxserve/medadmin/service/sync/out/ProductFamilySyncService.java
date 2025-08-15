package com.nexxserve.medadmin.service.sync.out;

import com.nexxserve.medadmin.dto.sync.ProductFamilySyncData;
import com.nexxserve.medadmin.repository.catalog.ProductFamilyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductFamilySyncService {

    private final ProductFamilyRepository productFamilyRepository;

    public Page<ProductFamilySyncData> findAll(Pageable pageable) {
        return productFamilyRepository.findAll(pageable).map(ProductFamilySyncData::fromEntity);
    }

    public Page<ProductFamilySyncData> findBySyncVersionGreaterThan(Double lastSyncVersion, Pageable pageable) {
        return productFamilyRepository.findBySyncVersionGreaterThan(lastSyncVersion, pageable).map(ProductFamilySyncData::fromEntity);
    }
}
