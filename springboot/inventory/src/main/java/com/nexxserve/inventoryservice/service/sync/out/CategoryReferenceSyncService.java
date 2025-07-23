package com.nexxserve.inventoryservice.service.sync.out;

import com.nexxserve.inventoryservice.dto.sync.CategoryReferenceSyncData;
import com.nexxserve.inventoryservice.repository.catalog.CategoryReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
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
        return orderingService.getAllOrderedCategories(pageable);
    }


}
