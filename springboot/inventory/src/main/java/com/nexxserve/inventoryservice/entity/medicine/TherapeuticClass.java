package com.nexxserve.inventoryservice.entity.medicine;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nexxserve.inventoryservice.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "classes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TherapeuticClass extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;


    @OneToMany(mappedBy = "therapeuticClass", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Generic> generics;


}
