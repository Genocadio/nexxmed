package com.nexxserve.medadmin.service.sync.out;

import com.nexxserve.medadmin.dto.sync.GenericSyncData;
import com.nexxserve.medadmin.repository.medicine.GenericRepository;
import com.nexxserve.medadmin.repository.medicine.TherapeuticClassRepository;
import com.nexxserve.medadmin.repository.sync.SyncSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenericSyncService {

    private final GenericRepository genericRepository;
    private final TherapeuticClassRepository therapeuticClassRepository;
    private final SyncSessionRepository syncSessionRepository;


    /**
     * Find all generics (for full sync)
     */
    public Page<GenericSyncData> findAll(Pageable pageable) {
        return genericRepository.findAll(pageable).map(GenericSyncData::fromEntity);
    }

    /**
     * Find generics with sync version greater than specified value
     */
    public Page<GenericSyncData> findBySyncVersionGreaterThan(Double lastSyncVersion, Pageable pageable) {
        return genericRepository.findBySyncVersionGreaterThan(lastSyncVersion, pageable).map(GenericSyncData::fromEntity);
    }

}