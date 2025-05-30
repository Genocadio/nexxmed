package com.nexxserve.billing.model;

        import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
        import lombok.Builder;
        import lombok.Data;
        import lombok.NoArgsConstructor;
        import org.hibernate.annotations.CreationTimestamp;
        import org.hibernate.annotations.UpdateTimestamp;

        import java.math.BigDecimal;
        import java.time.LocalDateTime;
        import java.util.ArrayList;
        import java.util.List;

        @Entity
        @Table(name = "service_activities")
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public class ServiceActivity {
            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            private Long id;

            @Column(nullable = false)
            private String name;

            @Column(nullable = false, precision = 10, scale = 2)
            private BigDecimal price;

            @Column(name = "service_id")
            private Long serviceId;

            @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
            private List<ActivityConsumable> activityConsumables = new ArrayList<>();

            @CreationTimestamp
            @Column(name = "created_at", nullable = false, updatable = false)
            private LocalDateTime createdAt;

            @UpdateTimestamp
            @Column(name = "updated_at")
            private LocalDateTime updatedAt;

            // Helper method to add a consumable with quantity
            public void addConsumable(Consumable consumable, Integer quantity) {
                ActivityConsumable activityConsumable = ActivityConsumable.builder()
                        .activity(this)
                        .consumable(consumable)
                        .quantity(quantity)
                        .build();
                activityConsumables.add(activityConsumable);
            }

            // Helper method to remove a consumable
            public void removeConsumable(Consumable consumable) {
                activityConsumables.removeIf(ac -> ac.getConsumable().equals(consumable));
            }

            // Helper method to get all consumables
            public List<Consumable> getConsumables() {
                return activityConsumables.stream()
                        .map(ActivityConsumable::getConsumable)
                        .toList();
            }
        }