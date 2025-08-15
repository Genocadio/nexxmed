package com.nexxserve.medadmin.service.sync.out;

import com.nexxserve.medadmin.dto.sync.MedicineInsuranceCoverageSyncData;
import com.nexxserve.medadmin.repository.medicine.MedicineInsuranceCoverageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MedicineInsuranceCoverageSyncService {
    private final MedicineInsuranceCoverageRepository medicineInsuranceCoverageRepository;

    public Page<MedicineInsuranceCoverageSyncData> findAll(Pageable pageable) {
        return medicineInsuranceCoverageRepository.findAll(pageable).map(MedicineInsuranceCoverageSyncData::fromEntity);
    }

    public Page<MedicineInsuranceCoverageSyncData> findBySyncVersionGreaterThan(
            Double lastSyncVersion,
            Pageable pageable
    ) {
        return medicineInsuranceCoverageRepository.findBySyncVersionGreaterThan(lastSyncVersion, pageable)
                .map(MedicineInsuranceCoverageSyncData::fromEntity);
    }
}

