package com.nexxserve.medadmin.service.sync.out;

import com.nexxserve.medadmin.dto.sync.TherapeuticClassSyncData;
import com.nexxserve.medadmin.repository.medicine.TherapeuticClassRepository;
import com.nexxserve.medadmin.repository.sync.SyncSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TherapeuticClassSyncService {

    private final TherapeuticClassRepository therapeuticClassRepository;
    private final SyncSessionRepository syncSessionRepository;

    public Page<TherapeuticClassSyncData> findAll(Pageable pageable) {
        return therapeuticClassRepository.findAll(pageable).map(TherapeuticClassSyncData::fromEntity);
    }

    /**
     * Find therapeutic classes with sync version greater than specified value
     */
    public Page<TherapeuticClassSyncData> findBySyncVersionGreaterThan(Double lastSyncVersion, Pageable pageable) {
        return therapeuticClassRepository.findBySyncVersionGreaterThan(lastSyncVersion, pageable).map(TherapeuticClassSyncData::fromEntity);
    }

}