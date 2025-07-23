package com.nexxserve.inventoryservice.service.sync.out;

import com.nexxserve.inventoryservice.dto.sync.CategoryReferenceSyncData;
import com.nexxserve.inventoryservice.entity.catalog.CategoryReference;
import com.nexxserve.inventoryservice.repository.catalog.CategoryReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Stream;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryReferenceOrderingSyncService {

    private final CategoryReferenceRepository categoryReferenceRepository;

    /**
     * Gets all categories in parent-first order
     */
    @Transactional(readOnly = true)
    public Page<CategoryReferenceSyncData> getAllOrderedCategories(Pageable pageable) {
        // Get the original page
        Page<CategoryReference> originalPage = categoryReferenceRepository.findAll(pageable);

        if (originalPage.isEmpty()) {
            return Page.empty(pageable);
        }

        // Collect categories from the current page
        List<CategoryReference> categories = new ArrayList<>(originalPage.getContent());

        // Find parent IDs that need to be included but aren't in the current page
        Set<UUID> parentIdsToInclude = new HashSet<>();
        Map<UUID, CategoryReference> categoryMap = new HashMap<>();

        // Map categories by ID and collect missing parent IDs
        for (CategoryReference category : categories) {
            categoryMap.put(category.getId(), category);
            if (category.getParent() != null && !categoryMap.containsKey(category.getParent().getId())) {
                parentIdsToInclude.add(category.getParent().getId());
            }
        }

        // Fetch missing parents
        List<CategoryReference> parentCategories = new ArrayList<>();
        if (!parentIdsToInclude.isEmpty()) {
            parentCategories = categoryReferenceRepository.findAllById(parentIdsToInclude);
            // Add parents to the map
            for (CategoryReference parent : parentCategories) {
                categoryMap.put(parent.getId(), parent);
            }
        }

        // Order categories - root categories first, then by parent-child relationship
        List<CategoryReference> orderedCategories = orderCategoriesByParentFirst(
                Stream.concat(parentCategories.stream(), categories.stream())
                      .collect(Collectors.toList())
        );

        // Convert to DTOs
        List<CategoryReferenceSyncData> result = orderedCategories.stream()
                .map(CategoryReferenceSyncData::fromEntity)
                .collect(Collectors.toList());

        // Create a new page with the ordered result
        return new PageImpl<>(result, pageable, originalPage.getTotalElements() + parentCategories.size());
    }

    /**
     * Gets categories for sync in parent-first order to prevent reference errors
     */
    @Transactional(readOnly = true)
    public Page<CategoryReferenceSyncData> getOrderedCategoriesForSync(Double lastSyncVersion, Pageable pageable) {
        // Get the original page
        Page<CategoryReference> originalPage = categoryReferenceRepository.findBySyncVersionGreaterThan(lastSyncVersion, pageable);

        if (originalPage.isEmpty()) {
            return Page.empty(pageable);
        }

        // Collect categories that need syncing
        List<CategoryReference> categories = new ArrayList<>(originalPage.getContent());

        // Find parent IDs that need to be included but aren't in the current page
        Set<UUID> parentIdsToInclude = new HashSet<>();
        Map<UUID, CategoryReference> categoryMap = new HashMap<>();

        // Map categories by ID and collect missing parent IDs
        for (CategoryReference category : categories) {
            categoryMap.put(category.getId(), category);
            if (category.getParent() != null && !categoryMap.containsKey(category.getParent().getId())) {
                parentIdsToInclude.add(category.getParent().getId());
            }
        }

        // Fetch missing parents
        List<CategoryReference> parentCategories = new ArrayList<>();
        if (!parentIdsToInclude.isEmpty()) {
            parentCategories = categoryReferenceRepository.findAllById(parentIdsToInclude);
            // Add parents to the map
            for (CategoryReference parent : parentCategories) {
                categoryMap.put(parent.getId(), parent);
            }
        }

        // Order categories - root categories first, then by parent-child relationship
        List<CategoryReference> orderedCategories = orderCategoriesByParentFirst(
                Stream.concat(parentCategories.stream(), categories.stream())
                      .collect(Collectors.toList())
        );

        // Convert to DTOs
        List<CategoryReferenceSyncData> result = orderedCategories.stream()
                .map(CategoryReferenceSyncData::fromEntity)
                .collect(Collectors.toList());

        // Create a new page with the ordered result
        return new PageImpl<>(result, pageable, originalPage.getTotalElements() + parentCategories.size());
    }

    /**
     * Orders categories so parents come before their children
     */
    private List<CategoryReference> orderCategoriesByParentFirst(List<CategoryReference> categories) {
        Map<UUID, CategoryReference> categoryMap = categories.stream()
                .collect(Collectors.toMap(CategoryReference::getId, c -> c));

        // Identify root categories (no parent or parent not in our set)
        List<CategoryReference> roots = categories.stream()
                .filter(c -> c.getParent() == null || !categoryMap.containsKey(c.getParent().getId()))
                .collect(Collectors.toList());

        // Create a dependency map (parent -> children)
        Map<UUID, List<CategoryReference>> childrenMap = new HashMap<>();
        categories.stream()
                .filter(c -> c.getParent() != null)
                .forEach(c -> {
                    UUID parentId = c.getParent().getId();
                    childrenMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(c);
                });

        // Use breadth-first traversal to build the ordered list
        List<CategoryReference> result = new ArrayList<>();
        Queue<CategoryReference> queue = new LinkedList<>(roots);

        while (!queue.isEmpty()) {
            CategoryReference current = queue.poll();
            result.add(current);

            List<CategoryReference> children = childrenMap.getOrDefault(current.getId(), Collections.emptyList());
            queue.addAll(children);
        }

        return result;
    }
}