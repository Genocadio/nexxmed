package com.nexxserve.medadmin.service.sync.out;

import com.nexxserve.medadmin.dto.sync.ProductInsuranceCoverageSyncData;
import com.nexxserve.medadmin.repository.catalog.ProductInsuranceCoverageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductInsuranceCoverageSyncService {

    private final ProductInsuranceCoverageRepository coverageRepository;

    public Page<ProductInsuranceCoverageSyncData> findAll(Pageable pageable) {
        return  coverageRepository.findAll(pageable).map(ProductInsuranceCoverageSyncData::fromEntity);

    }

    public Page<ProductInsuranceCoverageSyncData> findBySyncVersionGreaterThan(Double lastSyncVersion, Pageable pageable) {
        return coverageRepository.findBySyncVersionGreaterThan(lastSyncVersion, pageable)
                .map(ProductInsuranceCoverageSyncData::fromEntity);
    }
}

