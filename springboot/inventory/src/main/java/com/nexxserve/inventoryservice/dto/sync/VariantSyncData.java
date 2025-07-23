package com.nexxserve.inventoryservice.dto.sync;

import com.nexxserve.inventoryservice.entity.medicine.Variant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantSyncData {
    private UUID id;
    private String name;
    private String tradeName;
    private String form;
    private String route;
    private String strength;
    private String concentration;
    private String packaging;
    private String notes;
    private Double syncVersion;
    private Instant createdAt;
    private Instant updatedAt;

    // Parent relationships - only IDs to avoid nested objects
    private List<UUID> genericIds;

    public static VariantSyncData fromEntity(Variant entity) {
        return VariantSyncData.builder()
                .id(entity.getId())
                .name(entity.getName())
                .tradeName(entity.getTradeName())
                .form(entity.getForm())
                .route(entity.getRoute())
                .strength(entity.getStrength())
                .concentration(entity.getConcentration())
                .packaging(entity.getPackaging())
                .notes(entity.getNotes())
                .syncVersion(entity.getSyncVersion())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .genericIds(entity.getGenerics() != null ? entity.getGenerics().stream().map(g -> g.getId()).toList() : null)
                .build();
    }
    public Variant toEntity() {
        Variant variant = new Variant();
        variant.setId(this.id);
        variant.setName(this.name);
        variant.setTradeName(this.tradeName);
        variant.setForm(this.form);
        variant.setRoute(this.route);
        variant.setStrength(this.strength);
        variant.setConcentration(this.concentration);
        variant.setPackaging(this.packaging);
        variant.setNotes(this.notes);
        variant.setSyncVersion(this.syncVersion);
        variant.setCreatedAt(this.createdAt);
        variant.setUpdatedAt(this.updatedAt);

        return variant;
    }
}