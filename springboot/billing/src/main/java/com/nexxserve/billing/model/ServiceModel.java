package com.nexxserve.billing.model;

        import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
        import lombok.Builder;
        import lombok.Data;
        import lombok.NoArgsConstructor;
        import org.hibernate.annotations.CreationTimestamp;
        import org.hibernate.annotations.UpdateTimestamp;

        import java.time.LocalDateTime;
        import java.util.ArrayList;
        import java.util.List;

        @Entity
        @Table(name = "services")
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public class ServiceModel {
            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            private Long id;

            @Column(nullable = false)
            private String name;

            private String description;

            // Using ElementCollection to store just the IDs of related service activities
            @ElementCollection
            @CollectionTable(
                name = "service_activity_ids",
                joinColumns = @JoinColumn(name = "service_id")
            )
            @Column(name = "activity_id")
            @Builder.Default
            private List<Long> activityIds = new ArrayList<>();

            @CreationTimestamp
            @Column(name = "created_at", nullable = false, updatable = false)
            private LocalDateTime createdAt;

            @UpdateTimestamp
            @Column(name = "updated_at")
            private LocalDateTime updatedAt;

            // Helper method to add an activity ID
            public void addActivityId(Long activityId) {
                activityIds.add(activityId);
            }

            // Helper method to remove an activity ID
            public void removeActivityId(Long activityId) {
                activityIds.remove(activityId);
            }
        }