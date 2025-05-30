package com.nexxserve.billing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "activity_consumables")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityConsumable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = false)
    private ServiceActivity activity;

    @ManyToOne
    @JoinColumn(name = "consumable_id", nullable = false)
    private Consumable consumable;

    @Column(nullable = false)
    private Integer quantity;
}