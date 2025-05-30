package com.nexxserve.billing.service;

import com.nexxserve.billing.dto.ConsumableDto;
import com.nexxserve.billing.model.Consumable;
import com.nexxserve.billing.repository.ConsumableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsumableService {
    private final ConsumableRepository consumableRepository;

    public List<Consumable> getAllConsumables() {
        return consumableRepository.findAll();
    }

    public Optional<Consumable> getConsumableById(Long id) {
        return consumableRepository.findById(id);
    }

    public List<Consumable> findConsumablesByName(String name) {
        return consumableRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Consumable> findGlobalConsumables() {
        return consumableRepository.findByIsGlobalTrue();
    }

    public List<Consumable> findConsumablesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return consumableRepository.findByPriceBetween(minPrice, maxPrice);
    }

    @Transactional
    public Consumable createConsumable(ConsumableDto consumableDto) {
        Consumable consumable = Consumable.builder()
                .serviceId(consumableDto.getServiceId())
                .name(consumableDto.getName())
                .price(consumableDto.getPrice())
                .description(consumableDto.getDescription())
                .isGlobal(consumableDto.isGlobal())
                .build();

        return consumableRepository.save(consumable);
    }

    @Transactional
    public Optional<Consumable> updateConsumable(Long id, ConsumableDto updatedConsumableDto) {
        return consumableRepository.findById(id)
                .map(existingConsumable -> {
                    existingConsumable.setServiceId(updatedConsumableDto.getServiceId());
                    existingConsumable.setName(updatedConsumableDto.getName());
                    existingConsumable.setPrice(updatedConsumableDto.getPrice());
                    existingConsumable.setDescription(updatedConsumableDto.getDescription());
                    existingConsumable.setGlobal(updatedConsumableDto.isGlobal());
                    return consumableRepository.save(existingConsumable);
                });
    }

    @Transactional
    public boolean deleteConsumable(Long id) {
        return consumableRepository.findById(id)
                .map(consumable -> {
                    consumableRepository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }

    public ConsumableDto convertToDto(Consumable consumable) {
        return ConsumableDto.builder()
                .id(consumable.getId())
                .serviceId(consumable.getServiceId())
                .name(consumable.getName())
                .price(consumable.getPrice())
                .description(consumable.getDescription())
                .isGlobal(consumable.isGlobal())
                .build();
    }

    public List<ConsumableDto> convertToDtoList(List<Consumable> consumables) {
        return consumables.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}