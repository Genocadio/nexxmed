package com.nexxserve.medadmin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;


    @Column(name = "sync_version", precision = 3)
    private Double syncVersion = 0.0;

    @Transient
    private boolean isSyncOperation = false;

    @PrePersist
    protected void onCreate() {
        if(id == null) {
            id = UUID.randomUUID();
        }
        createdAt = Instant.now();
        updatedAt = Instant.now();
        syncVersion = 0.0;
    }

    @PreUpdate
    protected void onUpdate() {
        // Only auto-update timestamps and version if not a sync operation
        if (!isSyncOperation) {
            updatedAt = Instant.now();

            // Increment syncVersion when entity is updated
            if (syncVersion == null) {
                syncVersion = 1.0;
            } else {
                syncVersion += 0.1;
            }
        }
    }

    /**
     * Mark this entity as being updated as part of a sync operation,
     * which means timestamps and syncVersion should be preserved
     */
    public void markAsSyncOperation() {
        this.isSyncOperation = true;
    }

    /**
     * Gets the updatedAt timestamp as LocalDateTime
     * @return the updatedAt value converted to LocalDateTime
     */
    public LocalDateTime getUpdatedAtAsLocalDateTime() {
        return updatedAt != null ? LocalDateTime.ofInstant(updatedAt, ZoneId.systemDefault()) : null;
    }

    /**
     * Gets the createdAt timestamp as LocalDateTime
     * @return the createdAt value converted to LocalDateTime
     */
    public LocalDateTime getCreatedAtAsLocalDateTime() {
        return createdAt != null ? LocalDateTime.ofInstant(createdAt, ZoneId.systemDefault()) : null;
    }
}

