package com.nexxserve.billing.service;

import com.nexxserve.billing.dto.ConsumableQuantityDto;
import com.nexxserve.billing.dto.ServiceActivityDto;
import com.nexxserve.billing.model.Consumable;
import com.nexxserve.billing.model.ServiceActivity;
import com.nexxserve.billing.repository.ConsumableRepository;
import com.nexxserve.billing.repository.ServiceActivityRepository;
import com.nexxserve.billing.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceActivityService {
    private final ServiceActivityRepository serviceActivityRepository;
    private final ConsumableRepository consumableRepository;
    private final ServiceRepository serviceRepository;

    public List<ServiceActivity> getAllServiceActivities() {
        return serviceActivityRepository.findAll();
    }

    public Optional<ServiceActivity> getServiceActivityById(Long id) {
        return serviceActivityRepository.findById(id);
    }

    public List<ServiceActivity> findServiceActivitiesByName(String name) {
        return serviceActivityRepository.findByNameContainingIgnoreCase(name);
    }

    public List<ServiceActivity> findServiceActivitiesByServiceId(Long serviceId) {
        return serviceActivityRepository.findByServiceId(serviceId);
    }

    public List<ServiceActivity> findServiceActivitiesByMaxPrice(BigDecimal maxPrice) {
        return serviceActivityRepository.findByPriceLessThanEqual(maxPrice);
    }

    public List<ServiceActivity> findServiceActivitiesByConsumableId(Long consumableId) {
        return serviceActivityRepository.findByConsumableId(consumableId);
    }

    @Transactional
    public ServiceActivity createServiceActivity(ServiceActivityDto activityDto) {
        // Create and save the activity first
        ServiceActivity activity = ServiceActivity.builder()
                .name(activityDto.getName())
                .price(activityDto.getPrice())
                .serviceId(activityDto.getServiceId())
                .activityConsumables(new ArrayList<>()) // Initialize the list
                .build();

        ServiceActivity savedActivity = serviceActivityRepository.save(activity);

        // Add consumables with quantities if provided
        if (activityDto.getConsumables() != null && !activityDto.getConsumables().isEmpty()) {
            for (ConsumableQuantityDto consumableDto : activityDto.getConsumables()) {
                // Validate that consumable exists
                if (consumableDto.getConsumableId() == null) {
                    throw new IllegalArgumentException("Consumable ID cannot be null");
                }

                // Validate quantity
                if (consumableDto.getQuantity() == null || consumableDto.getQuantity() <= 0) {
                    throw new IllegalArgumentException("Quantity must be a positive number for consumable ID: " +
                            consumableDto.getConsumableId());
                }

                consumableRepository.findById(consumableDto.getConsumableId())
                        .ifPresentOrElse(
                                consumable -> savedActivity.addConsumable(consumable, consumableDto.getQuantity()),
                                () -> {
                                    throw new IllegalArgumentException("Consumable not found with ID: " +
                                            consumableDto.getConsumableId());
                                }
                        );
            }
            serviceActivityRepository.save(savedActivity);
        }

        // If the activity has a service ID, update the service
        if (activityDto.getServiceId() != null) {
            serviceRepository.findById(activityDto.getServiceId())
                    .ifPresent(service -> {
                        service.addActivityId(savedActivity.getId());
                        serviceRepository.save(service);
                    });
        }

        return savedActivity;
    }

    @Transactional
    public Optional<ServiceActivity> updateServiceActivity(Long id, ServiceActivityDto updatedActivity) {
        return serviceActivityRepository.findById(id)
                .map(existingActivity -> {
                    // Handle service ID change if needed
                    if (existingActivity.getServiceId() != null &&
                            !existingActivity.getServiceId().equals(updatedActivity.getServiceId())) {
                        // Remove activity ID from old service
                        serviceRepository.findById(existingActivity.getServiceId())
                                .ifPresent(oldService -> {
                                    oldService.removeActivityId(id);
                                    serviceRepository.save(oldService);
                                });
                    }

                    // Add to new service if service ID is provided
                    if (updatedActivity.getServiceId() != null) {
                        serviceRepository.findById(updatedActivity.getServiceId())
                                .ifPresent(newService -> {
                                    newService.addActivityId(id);
                                    serviceRepository.save(newService);
                                });
                    }

                    existingActivity.setName(updatedActivity.getName());
                    existingActivity.setPrice(updatedActivity.getPrice());
                    existingActivity.setServiceId(updatedActivity.getServiceId());

                    // Update consumables with quantities if provided
                    if (updatedActivity.getConsumables() != null) {
                        // Initialize activityConsumables if null
                        if (existingActivity.getActivityConsumables() == null) {
                            existingActivity.setActivityConsumables(new ArrayList<>());
                        } else {
                            // Clear existing activity consumables
                            existingActivity.getActivityConsumables().clear();
                        }

                        // Add new consumables with quantities
                        for (ConsumableQuantityDto consumableDto : updatedActivity.getConsumables()) {
                            // Validate that consumable exists
                            if (consumableDto.getConsumableId() == null) {
                                throw new IllegalArgumentException("Consumable ID cannot be null");
                            }

                            // Validate quantity
                            if (consumableDto.getQuantity() == null || consumableDto.getQuantity() <= 0) {
                                throw new IllegalArgumentException("Quantity must be a positive number for consumable ID: " +
                                        consumableDto.getConsumableId());
                            }

                            consumableRepository.findById(consumableDto.getConsumableId())
                                    .ifPresentOrElse(
                                            consumable -> existingActivity.addConsumable(consumable, consumableDto.getQuantity()),
                                            () -> {
                                                throw new IllegalArgumentException("Consumable not found with ID: " +
                                                        consumableDto.getConsumableId());
                                            }
                                    );
                        }
                    }

                    return serviceActivityRepository.save(existingActivity);
                });
    }


@Transactional
public Optional<ServiceActivity> addConsumableToActivity(Long activityId, Long consumableId, Integer quantity) {
    Optional<ServiceActivity> activityOpt = serviceActivityRepository.findById(activityId);
    Optional<Consumable> consumableOpt = consumableRepository.findById(consumableId);

    if (activityOpt.isPresent() && consumableOpt.isPresent()) {
        ServiceActivity activity = activityOpt.get();
        Consumable consumable = consumableOpt.get();

        activity.addConsumable(consumable, quantity);
        return Optional.of(serviceActivityRepository.save(activity));
    }

    return Optional.empty();
}
    @Transactional
    public boolean deleteServiceActivity(Long id) {
        return serviceActivityRepository.findById(id)
                .map(activity -> {
                    // If the activity has a service, remove the activity ID from the service
                    if (activity.getServiceId() != null) {
                        serviceRepository.findById(activity.getServiceId())
                                .ifPresent(service -> {
                                    service.removeActivityId(id);
                                    serviceRepository.save(service);
                                });
                    }

                    serviceActivityRepository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public Optional<ServiceActivity> removeConsumableFromActivity(Long activityId, Long consumableId) {
        Optional<ServiceActivity> activityOpt = serviceActivityRepository.findById(activityId);
        Optional<Consumable> consumableOpt = consumableRepository.findById(consumableId);

        if (activityOpt.isPresent() && consumableOpt.isPresent()) {
            ServiceActivity activity = activityOpt.get();
            Consumable consumable = consumableOpt.get();

            activity.removeConsumable(consumable);
            return Optional.of(serviceActivityRepository.save(activity));
        }

        return Optional.empty();
    }
}