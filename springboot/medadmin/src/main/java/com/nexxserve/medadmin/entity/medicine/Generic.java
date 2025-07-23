package com.nexxserve.medadmin.entity.medicine;

import com.nexxserve.medadmin.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "generics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Generic extends BaseEntity {


    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "chemical_name", length = 150)
    private String chemicalName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    @ToString.Exclude
    private TherapeuticClass therapeuticClass;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_parent")
    @Builder.Default
    private Boolean isParent = true;

    // Update to Generic class - add this field
    @OneToMany(mappedBy = "generic", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<MedicineInsuranceCoverage> insuranceCoverages;


    @ManyToMany(mappedBy = "generics", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Variant> variants;

}