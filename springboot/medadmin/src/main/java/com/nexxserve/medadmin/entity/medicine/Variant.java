package com.nexxserve.medadmin.entity.medicine;

import com.nexxserve.medadmin.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "variants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Variant extends BaseEntity {


    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 150)
    private String tradeName;

    @Column(length = 50)
    private String form;


    // Update to Variant class - add this field
    @OneToMany(mappedBy = "variant", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<MedicineInsuranceCoverage> insuranceCoverages;

    @Column(length = 50)
    private String route;

    @Column(length = 50)
    private String strength;

    @Column(length = 50)
    private String concentration;

    @Column(length = 100)
    private String packaging;

    @Column(columnDefinition = "TEXT")
    private String notes;



    // Many-to-Many relationship with Generic
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "variant_generics",
            joinColumns = @JoinColumn(name = "variant_id"),
            inverseJoinColumns = @JoinColumn(name = "generic_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Generic> generics;

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Brand> brands;

}