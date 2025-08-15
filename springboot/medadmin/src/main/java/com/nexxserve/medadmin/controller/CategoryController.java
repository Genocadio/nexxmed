package com.nexxserve.medadmin.controller;

import com.nexxserve.medadmin.dto.catalog.CategoryDto;
import com.nexxserve.medadmin.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> findAll() {
        log.debug("REST request to get all Categories");
        List<CategoryDto> categories = categoryService.findAll();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> findById(@PathVariable UUID id) {
        log.debug("REST request to get Category: {}", id);
        CategoryDto category = categoryService.findById(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<CategoryDto> findByCode(@PathVariable String code) {
        log.debug("REST request to get Category by code: {}", code);
        CategoryDto category = categoryService.findByCode(code);
        return ResponseEntity.ok(category);
    }

    @GetMapping("/root")
    public ResponseEntity<List<CategoryDto>> findRootCategories() {
        log.debug("REST request to get root categories");
        List<CategoryDto> categories = categoryService.findRootCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<CategoryDto>> findByParentId(@PathVariable UUID parentId) {
        log.debug("REST request to get categories by parent ID: {}", parentId);
        List<CategoryDto> categories = categoryService.findByParentId(parentId);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/active")
    public ResponseEntity<List<CategoryDto>> findActiveCategories() {
        log.debug("REST request to get active categories");
        List<CategoryDto> categories = categoryService.findActiveCategories();
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<CategoryDto> create(@Valid @RequestBody CategoryDto categoryDto) {
        log.debug("REST request to save Category: {}", categoryDto.getName());
        CategoryDto result = categoryService.create(categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryDto categoryDto) {
        log.debug("REST request to update Category: {}", id);
        CategoryDto result = categoryService.update(id, categoryDto);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.debug("REST request to delete Category: {}", id);
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}