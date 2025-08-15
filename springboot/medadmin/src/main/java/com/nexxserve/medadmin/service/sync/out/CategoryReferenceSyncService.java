package com.nexxserve.medadmin.service.sync.out;

import com.nexxserve.medadmin.dto.sync.CategoryReferenceSyncData;
import com.nexxserve.medadmin.repository.catalog.CategoryReferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryReferenceSyncService {

    private final CategoryReferenceRepository categoryReferenceRepository;
    private final CategoryReferenceOrderingSyncService orderingService;

    public Page<CategoryReferenceSyncData> findBySyncVersionGreaterThan(
            Double lastSyncVersion,
           Pageable pageable) {
        return orderingService.getOrderedCategoriesForSync(lastSyncVersion, pageable);
    }
    public Page<CategoryReferenceSyncData> findAll(Pageable pageable) {
        log.info("Getting all categories for sync");
        return orderingService.getAllOrderedCategories(pageable);
    }


}
