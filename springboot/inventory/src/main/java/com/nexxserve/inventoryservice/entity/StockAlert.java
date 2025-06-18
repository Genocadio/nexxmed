package com.nexxserve.inventoryservice.entity;


import com.nexxserve.inventoryservice.enums.AlertStatus;
import com.nexxserve.inventoryservice.enums.AlertType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock_alerts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_entry_id", nullable = false)
    private StockEntry stockEntry;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false)
    private AlertType alertType;

    @Column(name = "threshold_value")
    private Integer threshold;

    @Column(name = "current_quantity")
    private Integer currentQuantity;

    @Column(name = "alert_date", nullable = false)
    private LocalDateTime alertDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private AlertStatus status = AlertStatus.ACTIVE;

    @Column(name = "message")
    private String message;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (alertDate == null) {
            alertDate = LocalDateTime.now();
        }
    }
}