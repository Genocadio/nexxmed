package com.nexxserve.catalog.model.entity;

import com.nexxserve.catalog.enums.ImageType;
import com.nexxserve.catalog.model.valueobjects.ImageDimensions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "product_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String url;

    private String label;

    @Column(nullable = false)
    private String altText;

    @Column(nullable = false)
    private Integer position;

    @Column(nullable = false)
    private Boolean isPrimary = false;

    @Enumerated(EnumType.STRING)
    private ImageType imageType;

    @Embedded
    private ImageDimensions dimensions;

    private Long fileSize;
    private String format;
    private String copyrightInfo;

    @ElementCollection
    @CollectionTable(name = "image_usage_rights")
    private List<String> usageRights;
}