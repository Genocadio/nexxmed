package com.nexxserve.medadmin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;


@Entity
@Table(name = "insurances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Insurance extends BaseEntity {

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "abbreviation", length = 50)
    private String abbreviation;

    @Column(name = "defaultClientContributionPercentage", precision = 5, scale = 2)
    private BigDecimal defaultClientContributionPercentage;

    @Column(name = "default_requires_pre_approval")
    private Boolean defaultRequiresPreApproval;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private String updatedBy;
}