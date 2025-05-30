package com.nexxserve.billing.controller;

import com.nexxserve.billing.dto.*;
import com.nexxserve.billing.model.Consumable;
import com.nexxserve.billing.model.ServiceActivity;
import com.nexxserve.billing.model.ServiceModel;
import com.nexxserve.billing.service.ConsumableService;
import com.nexxserve.billing.service.ServiceActivityService;
import com.nexxserve.billing.service.ServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/billing")
@RequiredArgsConstructor
public class BillingController {

    private final ConsumableService consumableService;
    private final ServiceActivityService serviceActivityService;
    private final ServiceService serviceService;
    @Value("${spring.application.name:Billing Service}")
    private String serviceName;



    @GetMapping("/permissions")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<PermissionsResponseDto> getRolesAndPermissions() {
        // Creating hardcoded permissions for different roles
        List<PermissionsDto> permissions = new ArrayList<>();

        // Admin permissions
        permissions.add(new PermissionsDto("billing.service.create", "Create billing services", "ADMIN"));
        permissions.add(new PermissionsDto("billing.service.read", "Read billing services", "ADMIN"));
        permissions.add(new PermissionsDto("billing.service.update", "Update billing services", "ADMIN"));
        permissions.add(new PermissionsDto("billing.service.delete", "Delete billing services", "ADMIN"));

        permissions.add(new PermissionsDto("billing.activity.create", "Create service activities", "ADMIN"));
        permissions.add(new PermissionsDto("billing.activity.read", "Read service activities", "ADMIN"));
        permissions.add(new PermissionsDto("billing.activity.update", "Update service activities", "ADMIN"));
        permissions.add(new PermissionsDto("billing.activity.delete", "Delete service activities", "ADMIN"));

        permissions.add(new PermissionsDto("billing.consumable.create", "Create consumables", "ADMIN"));
        permissions.add(new PermissionsDto("billing.consumable.read", "Read consumables", "ADMIN"));
        permissions.add(new PermissionsDto("billing.consumable.update", "Update consumables", "ADMIN"));
        permissions.add(new PermissionsDto("billing.consumable.delete", "Delete consumables", "ADMIN"));

        // Manager permissions
        permissions.add(new PermissionsDto("billing.service.read", "Read billing services", "MANAGER"));
        permissions.add(new PermissionsDto("billing.activity.read", "Read service activities", "MANAGER"));
        permissions.add(new PermissionsDto("billing.activity.create", "Create service activities", "MANAGER"));
        permissions.add(new PermissionsDto("billing.activity.update", "Update service activities", "MANAGER"));
        permissions.add(new PermissionsDto("billing.consumable.read", "Read consumables", "MANAGER"));

        // User permissions
        permissions.add(new PermissionsDto("billing.service.read", "Read billing services", "USER"));
        permissions.add(new PermissionsDto("billing.activity.read", "Read service activities", "USER"));
        permissions.add(new PermissionsDto("billing.consumable.read", "Read consumables", "USER"));

        PermissionsResponseDto response = new PermissionsResponseDto(permissions, serviceName);
        return ResponseEntity.ok(response);
    }

    // Consumable endpoints
    @GetMapping("/consumables")
    public ResponseEntity<List<Consumable>> getAllConsumables() {
        return ResponseEntity.ok(consumableService.getAllConsumables());
    }

    @GetMapping("/consumables/{id}")
    public ResponseEntity<Consumable> getConsumableById(@PathVariable Long id) {
        return consumableService.getConsumableById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/consumables/search")
    public ResponseEntity<List<Consumable>> searchConsumables(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean isGlobal) {

        if (name != null) {
            return ResponseEntity.ok(consumableService.findConsumablesByName(name));
        } else if (minPrice != null && maxPrice != null) {
            return ResponseEntity.ok(consumableService.findConsumablesByPriceRange(minPrice, maxPrice));
        } else if (Boolean.TRUE.equals(isGlobal)) {
            return ResponseEntity.ok(consumableService.findGlobalConsumables());
        }

        return ResponseEntity.ok(consumableService.getAllConsumables());
    }

    @PostMapping("/consumables")
    public ResponseEntity<Consumable> createConsumable(@Valid @RequestBody ConsumableDto consumableDto) {
        return new ResponseEntity<>(consumableService.createConsumable(consumableDto), HttpStatus.CREATED);
    }

    @PutMapping("/consumables/{id}")
    public ResponseEntity<Consumable> updateConsumable(@PathVariable Long id, @Valid @RequestBody ConsumableDto consumableDto) {
        return consumableService.updateConsumable(id, consumableDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/consumables/{id}")
    public ResponseEntity<Void> deleteConsumable(@PathVariable Long id) {
        return consumableService.deleteConsumable(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    // Service Activity endpoints
    @GetMapping("/activities")
    public ResponseEntity<List<ServiceActivityResponseDto>> getAllServiceActivities() {
        List<ServiceActivity> activities = serviceActivityService.getAllServiceActivities();
        return ResponseEntity.ok(ServiceActivityResponseDto.fromEntities(activities));
    }

    @GetMapping("/activities/{id}")
    public ResponseEntity<ServiceActivityResponseDto> getServiceActivityById(@PathVariable Long id) {
        return serviceActivityService.getServiceActivityById(id)
                .map(activity -> ResponseEntity.ok(ServiceActivityResponseDto.fromEntity(activity)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/activities/search")
    public ResponseEntity<List<ServiceActivityResponseDto>> searchServiceActivities(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long serviceId,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Long consumableId) {

        List<ServiceActivity> activities;
        if (name != null) {
            activities = serviceActivityService.findServiceActivitiesByName(name);
        } else if (serviceId != null) {
            activities = serviceActivityService.findServiceActivitiesByServiceId(serviceId);
        } else if (maxPrice != null) {
            activities = serviceActivityService.findServiceActivitiesByMaxPrice(maxPrice);
        } else if (consumableId != null) {
            activities = serviceActivityService.findServiceActivitiesByConsumableId(consumableId);
        } else {
            activities = serviceActivityService.getAllServiceActivities();
        }

        return ResponseEntity.ok(ServiceActivityResponseDto.fromEntities(activities));
    }

    @PostMapping("/activities")
    public ResponseEntity<ServiceActivityResponseDto> createServiceActivity(@Valid @RequestBody ServiceActivityDto activityDto) {
        ServiceActivity activity = serviceActivityService.createServiceActivity(activityDto);
        return new ResponseEntity<>(ServiceActivityResponseDto.fromEntity(activity), HttpStatus.CREATED);
    }

    @PutMapping("/activities/{id}")
    public ResponseEntity<ServiceActivityResponseDto> updateServiceActivity(@PathVariable Long id, @Valid @RequestBody ServiceActivityDto activityDto) {
        return serviceActivityService.updateServiceActivity(id, activityDto)
                .map(activity -> ResponseEntity.ok(ServiceActivityResponseDto.fromEntity(activity)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/activities/{id}")
    public ResponseEntity<Void> deleteServiceActivity(@PathVariable Long id) {
        return serviceActivityService.deleteServiceActivity(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/activities/{activityId}/consumables/{consumableId}")
    public ResponseEntity<ServiceActivityResponseDto> addConsumableToActivity(@PathVariable Long activityId, @PathVariable Long consumableId, @PathVariable Integer quantity) {
        return serviceActivityService.addConsumableToActivity(activityId, consumableId, quantity)
                .map(activity -> ResponseEntity.ok(ServiceActivityResponseDto.fromEntity(activity)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/activities/{activityId}/consumables/{consumableId}")
    public ResponseEntity<ServiceActivityResponseDto> removeConsumableFromActivity(@PathVariable Long activityId, @PathVariable Long consumableId) {
        return serviceActivityService.removeConsumableFromActivity(activityId, consumableId)
                .map(activity -> ResponseEntity.ok(ServiceActivityResponseDto.fromEntity(activity)))
                .orElse(ResponseEntity.badRequest().build());
    }

    // Service endpoints
    @GetMapping("/services")
    public ResponseEntity<List<ServiceModel>> getAllServices() {
        return ResponseEntity.ok(serviceService.getAllServices());
    }

    @GetMapping("/services/{id}")
    public ResponseEntity<ServiceModel> getServiceById(@PathVariable Long id) {
        return serviceService.getServiceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/services/search")
    public ResponseEntity<List<ServiceModel>> searchServices(@RequestParam String name) {
        return ResponseEntity.ok(serviceService.findServicesByName(name));
    }

    @GetMapping("/services/activity/{activityId}")
    public ResponseEntity<ServiceModel> getServiceByActivityId(@PathVariable Long activityId) {
        ServiceModel service = serviceService.findByActivityId(activityId);
        return service != null
                ? ResponseEntity.ok(service)
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/services")
    public ResponseEntity<ServiceModel> createService(@Valid @RequestBody ServiceDto serviceDto) {
        return new ResponseEntity<>(serviceService.createService(serviceDto), HttpStatus.CREATED);
    }

    @PutMapping("/services/{id}")
    public ResponseEntity<ServiceModel> updateService(@PathVariable Long id, @Valid @RequestBody ServiceDto serviceDto) {
        return serviceService.updateService(id, serviceDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/services/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        return serviceService.deleteService(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/services/{serviceId}/activities/{activityId}")
    public ResponseEntity<ServiceModel> addActivityToService(@PathVariable Long serviceId, @PathVariable Long activityId) {
        return serviceService.addActivityToService(serviceId, activityId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/services/{serviceId}/activities/{activityId}")
    public ResponseEntity<ServiceModel> removeActivityFromService(@PathVariable Long serviceId, @PathVariable Long activityId) {
        return serviceService.removeActivityFromService(serviceId, activityId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }
}