package com.nexxserve.catalog.service;

import com.nexxserve.catalog.dto.CategoryDto;
import com.nexxserve.catalog.exception.CategoryInUseException;
import com.nexxserve.catalog.mapper.CategoryMapper;
import com.nexxserve.catalog.model.entity.CategoryReference;
import com.nexxserve.catalog.repository.CategoryReferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryReferenceRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Cacheable(value = "allCategories")
    public List<CategoryDto> findAll() {
        log.debug("Finding all categories");
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Cacheable(value = "categories", key = "#id")
    public CategoryDto findById(UUID id) {
        log.debug("Finding category with id: {}", id);
        CategoryReference category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return categoryMapper.toDto(category);
    }

    public CategoryDto findByCode(String code) {
        log.debug("Finding category with code: {}", code);
        CategoryReference category = categoryRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Category not found with code: " + code));
        return categoryMapper.toDto(category);
    }

    @Cacheable(value = "rootCategories")
    public List<CategoryDto> findRootCategories() {
        log.debug("Finding root categories");
        return categoryRepository.findRootCategories()
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    public List<CategoryDto> findByParentId(UUID parentId) {
        log.debug("Finding categories by parent id: {}", parentId);
        return categoryRepository.findByParentId(parentId)
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Cacheable(value = "activeCategories")
    public List<CategoryDto> findActiveCategories() {
        log.debug("Finding active categories");
        return categoryRepository.findByIsActiveTrueOrderByDisplayOrder()
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Transactional
    @CacheEvict(value = {"categories", "rootCategories", "activeCategories"}, allEntries = true)
    public CategoryDto create(CategoryDto categoryDto) {
        log.debug("Creating category: {}", categoryDto.getName());

        // Check for duplicate code
        if (categoryRepository.findByCode(categoryDto.getCode()).isPresent()) {
            throw new RuntimeException("Category with code already exists: " + categoryDto.getCode());
        }

        CategoryReference category = categoryMapper.toEntity(categoryDto);

        // If there's a parent reference, ensure it exists in database
        if (category.getParent() != null && category.getParent().getId() != null) {
            CategoryReference parent = categoryRepository.findById(category.getParent().getId())
                .orElseThrow(() -> new RuntimeException("Parent category not found with id: " +
                             category.getParent().getId()));
            category.setParent(parent);
        }

        CategoryReference savedCategory = categoryRepository.save(category);
        log.info("Created category with id: {}", savedCategory.getId());
        return categoryMapper.toDto(savedCategory);
    }

    @Transactional
    @CacheEvict(value = {"categories", "rootCategories", "activeCategories"}, allEntries = true)
    public CategoryDto update(UUID id, CategoryDto categoryDto) {
        log.debug("Updating category with id: {}", id);
        CategoryReference existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        // Check for duplicate code if code is being changed
        if (!existingCategory.getCode().equals(categoryDto.getCode()) &&
                categoryRepository.findByCode(categoryDto.getCode()).isPresent()) {
            throw new RuntimeException("Category with code already exists: " + categoryDto.getCode());
        }

        categoryMapper.updateEntityFromDto(categoryDto, existingCategory);
        CategoryReference savedCategory = categoryRepository.save(existingCategory);
        log.info("Updated category with id: {}", savedCategory.getId());
        return categoryMapper.toDto(savedCategory);
    }

    @Transactional
    @CacheEvict(value = {"categories", "rootCategories", "activeCategories"}, allEntries = true)
    public void delete(UUID id) {
        log.debug("Deleting category with id: {}", id);
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }

        boolean hasReferences = categoryRepository.hasProductFamilyReferences(id);
        if (hasReferences) {
            throw new CategoryInUseException(id);
        }
        categoryRepository.deleteById(id);
        log.info("Deleted category with id: {}", id);
    }
}