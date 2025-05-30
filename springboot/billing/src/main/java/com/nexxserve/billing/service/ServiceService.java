package com.nexxserve.billing.service;

import com.nexxserve.billing.dto.ServiceDto;
import com.nexxserve.billing.model.ServiceModel;
import com.nexxserve.billing.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ServiceService {
    private final ServiceRepository serviceRepository;

    public List<ServiceModel> getAllServices() {
        return serviceRepository.findAll();
    }

    public Optional<ServiceModel> getServiceById(Long id) {
        return serviceRepository.findById(id);
    }

    public List<ServiceModel> findServicesByName(String name) {
        return serviceRepository.findByNameContainingIgnoreCase(name);
    }

    public ServiceModel findByActivityId(Long activityId) {
        return serviceRepository.findByActivityId(activityId);
    }

   @Transactional
   public ServiceModel createService(ServiceDto serviceDto) {
       ServiceModel service = ServiceModel.builder()
           .name(serviceDto.getName())
           .description(serviceDto.getDescription())
           .build();

       return serviceRepository.save(service);
   }

    @Transactional
    public Optional<ServiceModel> updateService(Long id, ServiceDto updatedService) {
        return serviceRepository.findById(id)
                .map(existingService -> {
                    existingService.setName(updatedService.getName());
                    existingService.setDescription(updatedService.getDescription());
                    return serviceRepository.save(existingService);
                });
    }

    @Transactional
    public boolean deleteService(Long id) {
        if (serviceRepository.existsById(id)) {
            serviceRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public Optional<ServiceModel> addActivityToService(Long serviceId, Long activityId) {
        return serviceRepository.findById(serviceId)
                .map(service -> {
                    service.addActivityId(activityId);
                    return serviceRepository.save(service);
                });
    }

    @Transactional
    public Optional<ServiceModel> removeActivityFromService(Long serviceId, Long activityId) {
        return serviceRepository.findById(serviceId)
                .map(service -> {
                    service.removeActivityId(activityId);
                    return serviceRepository.save(service);
                });
    }
}