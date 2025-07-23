package com.nexxserve.inventoryservice.service.impl;

import com.nexxserve.inventoryservice.dto.Insurance.InsuranceRequestDto;
import com.nexxserve.inventoryservice.entity.Insurance;
import com.nexxserve.inventoryservice.exception.DuplicateEntryException;
import com.nexxserve.inventoryservice.repository.InsuranceRepository;
import com.nexxserve.inventoryservice.service.InsuranceService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InsuranceServiceImpl implements InsuranceService {

    private final InsuranceRepository insuranceRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public InsuranceServiceImpl(InsuranceRepository insuranceRepository) {
        this.insuranceRepository = insuranceRepository;
    }

    @Override
    @Transactional
    public Page<Insurance> findAll(Pageable pageable) {
        return insuranceRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Page<Insurance> findBySyncVersionGreaterThan(Double version, Pageable pageable) {
        return insuranceRepository.findBySyncVersionGreaterThan(version, pageable);
    }

    @Override
    @Transactional
    public Insurance createInsurance(InsuranceRequestDto requestDto, String username) {
        if (insuranceRepository.existsByName(requestDto.getName())) {
            throw new DuplicateEntryException("Insurance with name '" + requestDto.getName() + "' already exists.");
        }
        if (insuranceRepository.existsByAbbreviation(requestDto.getAbbreviation())) {
            throw new DuplicateEntryException("Insurance with abbreviation '" + requestDto.getAbbreviation() + "' already exists.");
        }
        Insurance insurance = Insurance.builder()
                .name(requestDto.getName())
                .abbreviation(requestDto.getAbbreviation())
                .defaultClientContributionPercentage(requestDto.getDefaultClientContributionPercentage())
                .defaultRequiresPreApproval(requestDto.getDefaultRequiresPreApproval())
                .active(requestDto.getActive())
                .createdBy(username)
                .updatedBy(username)
                .build();

        return insuranceRepository.save(insurance);
    }

    @Override
    @Transactional
    public Insurance updateInsurance(UUID id, InsuranceRequestDto requestDto, String username) {
        Optional<Insurance> optionalInsurance = insuranceRepository.findById(id);
        if (optionalInsurance.isPresent()) {
            Insurance insurance = optionalInsurance.get();
            if (insuranceRepository.existsByName(requestDto.getName()) &&
                !insurance.getName().equals(requestDto.getName())) {
                throw new DuplicateEntryException("Insurance with name '" + requestDto.getName() + "' already exists.");
            }
            if (insuranceRepository.existsByAbbreviation(requestDto.getAbbreviation()) &&
                !insurance.getAbbreviation().equals(requestDto.getAbbreviation())) {
                throw new DuplicateEntryException("Insurance with abbreviation '" + requestDto.getAbbreviation() + "' already exists.");
            }
            insurance.setName(requestDto.getName());
            insurance.setAbbreviation(requestDto.getAbbreviation());
            insurance.setDefaultClientContributionPercentage(requestDto.getDefaultClientContributionPercentage());
            insurance.setDefaultRequiresPreApproval(requestDto.getDefaultRequiresPreApproval());
            insurance.setActive(requestDto.getActive());
            insurance.setUpdatedBy(username);
            return insuranceRepository.save(insurance);
        }
        return null;
    }

    public List<Insurance> findBySyncVersionGreaterThan(Double version) {
        return insuranceRepository.findBySyncVersionGreaterThan(version);
    }

    @Override
    @Transactional
    public void deleteInsurance(UUID id) {
        insuranceRepository.deleteById(id);
    }

    @Override
    public Optional<Insurance> findById(UUID id) {
        return insuranceRepository.findById(id);
    }

    @Override
    public Optional<Insurance> findByName(String name) {
        return insuranceRepository.findByName(name);
    }

    @Override
    public List<Insurance> findByActive(Boolean active) {
        return insuranceRepository.findByActive(active);
    }

    @Override
    public List<Insurance> findAll() {
        return insuranceRepository.findAll();
    }

    @Override
    public List<Insurance> search(String query) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Insurance> cq = cb.createQuery(Insurance.class);
        Root<Insurance> root = cq.from(Insurance.class);

        String searchTerm = "%" + query.toLowerCase() + "%";

        Predicate namePredicate = cb.like(cb.lower(root.get("name")), searchTerm);
        Predicate abbreviationPredicate = cb.like(cb.lower(root.get("abbreviation")), searchTerm);

        cq.where(cb.or(namePredicate, abbreviationPredicate));

        return entityManager.createQuery(cq).getResultList();
    }
}