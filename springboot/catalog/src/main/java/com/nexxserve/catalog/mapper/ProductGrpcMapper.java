package com.nexxserve.catalog.mapper;

import com.google.protobuf.StringValue;
import com.google.protobuf.Timestamp;
import com.nexxserve.catalog.dto.*;
import com.nexxserve.catalog.enums.*;
import com.nexxserve.catalog.grpc.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductGrpcMapper {

    public GrpcProductFamilyDetails toGrpcProductFamily(ProductFamilyDto dto) {
        if (dto == null) {
            return null;
        }

        GrpcProductFamilyDetails.Builder builder = GrpcProductFamilyDetails.newBuilder()
                .setId(dto.getId().toString())
                .setName(dto.getName())
                .setStatus(mapProductStatus(dto.getStatus()))
                .setLifecycleStage(mapLifecycleStage(dto.getLifecycleStage()))
                .setHazardClass(mapHazardClass(dto.getHazardClass()));

        // Set nullable string fields
        if (dto.getDescription() != null) {
            builder.setDescription(StringValue.of(dto.getDescription()));
        }
        if (dto.getShortDescription() != null) {
            builder.setShortDescription(StringValue.of(dto.getShortDescription()));
        }
        if (dto.getSearchKeywords() != null) {
            builder.setSearchKeywords(StringValue.of(dto.getSearchKeywords()));
        }
        if (dto.getBrand() != null) {
            builder.setBrand(StringValue.of(dto.getBrand()));
        }

        // Set CategoryDetails if present
        if (dto.getCategory() != null) {
            builder.setCategory(mapCategory(dto.getCategory()));
        }

        // Set nullable boolean fields
        if (dto.getAgeRestricted() != null) {
            builder.setAgeRestricted(com.google.protobuf.BoolValue.of(dto.getAgeRestricted()));
        }

        // Set lists
        if (dto.getSubCategoryIds() != null) {
            builder.addAllSubCategoryIds(dto.getSubCategoryIds().stream()
                    .map(Object::toString)
                    .collect(Collectors.toList()));
        }
        if (dto.getTags() != null) {
            builder.addAllTags(dto.getTags());
        }
        if (dto.getCertifications() != null) {
            builder.addAllCertifications(dto.getCertifications());
        }

        // Set date fields
        if (dto.getLaunchDate() != null) {
            builder.setLaunchDate(toGrpcTimestamp(dto.getLaunchDate()));
        }
        if (dto.getDiscontinueDate() != null) {
            builder.setDiscontinueDate(toGrpcTimestamp(dto.getDiscontinueDate()));
        }
        if (dto.getCreatedAt() != null) {
            builder.setCreatedAt(toGrpcTimestamp(dto.getCreatedAt()));
        }
        if (dto.getUpdatedAt() != null) {
            builder.setUpdatedAt(toGrpcTimestamp(dto.getUpdatedAt()));
        }

        // Set user tracking fields
        if (dto.getCreatedBy() != null) {
            builder.setCreatedBy(StringValue.of(dto.getCreatedBy()));
        }
        if (dto.getUpdatedBy() != null) {
            builder.setUpdatedBy(StringValue.of(dto.getUpdatedBy()));
        }
        if (dto.getVersion() != null) {
            builder.setVersion(com.google.protobuf.Int32Value.of(dto.getVersion()));
        }

        return builder.build();
    }

    public ProductVariantDetails toGrpcProductVariant(ProductVariantDto dto) {
        if (dto == null) {
            return null;
        }

        ProductVariantDetails.Builder builder = ProductVariantDetails.newBuilder()
                .setId(dto.getId().toString())
                .setName(dto.getName())
                .setSku(dto.getSku())
                .setBrand(dto.getBrand())
                .setUnitOfMeasure(mapUnitOfMeasure(dto.getUnitOfMeasure()))
                .setStatus(mapProductStatus(dto.getStatus()));

        // Set family if present
        if (dto.getFamily() != null) {
            builder.setFamily(toGrpcProductFamily(dto.getFamily()));
        }

        // Set nullable string fields
        if (dto.getDisplayName() != null) {
            builder.setDisplayName(StringValue.of(dto.getDisplayName()));
        }
        if (dto.getUpc() != null) {
            builder.setUpc(StringValue.of(dto.getUpc()));
        }
        if (dto.getGtin() != null) {
            builder.setGtin(StringValue.of(dto.getGtin()));
        }
        if (dto.getManufacturer() != null) {
            builder.setManufacturer(StringValue.of(dto.getManufacturer()));
        }
        if (dto.getManufacturerPartNumber() != null) {
            builder.setManufacturerPartNumber(StringValue.of(dto.getManufacturerPartNumber()));
        }
        if (dto.getCountryOfOrigin() != null) {
            builder.setCountryOfOrigin(StringValue.of(dto.getCountryOfOrigin()));
        }
        if (dto.getColor() != null) {
            builder.setColor(StringValue.of(dto.getColor()));
        }
        if (dto.getSearchKeywords() != null) {
            builder.setSearchKeywords(StringValue.of(dto.getSearchKeywords()));
        }
        if (dto.getSeoUrl() != null) {
            builder.setSeoUrl(StringValue.of(dto.getSeoUrl()));
        }
        if (dto.getMetaDescription() != null) {
            builder.setMetaDescription(StringValue.of(dto.getMetaDescription()));
        }

        // Set lists
        if (dto.getBarcodes() != null) {
            builder.addAllBarcodes(dto.getBarcodes());
        }
        if (dto.getMaterial() != null) {
            builder.addAllMaterial(dto.getMaterial());
        }
        if (dto.getAllergens() != null) {
            builder.addAllAllergens(dto.getAllergens());
        }
        if (dto.getIngredients() != null) {
            builder.addAllIngredients(dto.getIngredients());
        }
        if (dto.getWarnings() != null) {
            builder.addAllWarnings(dto.getWarnings());
        }
        if (dto.getInstructions() != null) {
            builder.addAllInstructions(dto.getInstructions());
        }
        if (dto.getCompatibility() != null) {
            builder.addAllCompatibility(dto.getCompatibility());
        }
        if (dto.getQualityCertifications() != null) {
            builder.addAllQualityCertifications(dto.getQualityCertifications());
        }

        // Set maps
        if (dto.getAttributes() != null) {
            builder.putAllAttributes(dto.getAttributes());
        }
        if (dto.getSpecifications() != null) {
            builder.putAllSpecifications(dto.getSpecifications());
        }

        // Set numeric fields
        if (dto.getUnitsPerPackage() != null) {
            builder.setUnitsPerPackage(com.google.protobuf.Int32Value.of(dto.getUnitsPerPackage()));
        }
        if (dto.getAverageRating() != null) {
            builder.setAverageRating(com.google.protobuf.DoubleValue.of(dto.getAverageRating().doubleValue()));
        }
        if (dto.getReviewCount() != null) {
            builder.setReviewCount(com.google.protobuf.Int32Value.of(dto.getReviewCount()));
        }

        // Set boolean fields
        if (dto.getIsLimitedEdition() != null) {
            builder.setIsLimitedEdition(com.google.protobuf.BoolValue.of(dto.getIsLimitedEdition()));
        }

        // Set timestamps
        if (dto.getCreatedAt() != null) {
            builder.setCreatedAt(toGrpcTimestamp(dto.getCreatedAt()));
        }
        if (dto.getUpdatedAt() != null) {
            builder.setUpdatedAt(toGrpcTimestamp(dto.getUpdatedAt()));
        }

        // Set user tracking fields
        if (dto.getCreatedBy() != null) {
            builder.setCreatedBy(StringValue.of(dto.getCreatedBy()));
        }
        if (dto.getUpdatedBy() != null) {
            builder.setUpdatedBy(StringValue.of(dto.getUpdatedBy()));
        }
        if (dto.getVersion() != null) {
            builder.setVersion(com.google.protobuf.Int32Value.of(dto.getVersion()));
        }

        // Set insurance coverages
        if (dto.getInsuranceCoverages() != null && !dto.getInsuranceCoverages().isEmpty()) {
            dto.getInsuranceCoverages().forEach(coverage ->
                builder.addInsuranceCoverages(toGrpcInsuranceCoverage(coverage)));
        }

        return builder.build();
    }

    private ProductInsuranceCoverageDetails toGrpcInsuranceCoverage(ProductInsuranceCoverageDto dto) {
        if (dto == null) {
            return null;
        }

        ProductInsuranceCoverageDetails.Builder builder = ProductInsuranceCoverageDetails.newBuilder()
                .setId(dto.getId().toString())
                .setInsuranceId(dto.getInsuranceId().toString())
                .setInsuranceName(dto.getInsuranceName())
                .setStatus(mapCoverageStatus(dto.getStatus()))
                .setApprovalType(mapApprovalType(dto.getApprovalType()));

        // Set nullable fields
        if (dto.getProductFamilyId() != null) {
            builder.setProductFamilyId(StringValue.of(dto.getProductFamilyId().toString()));
        }
        if (dto.getProductFamilyName() != null) {
            builder.setProductFamilyName(StringValue.of(dto.getProductFamilyName()));
        }
        if (dto.getProductVariantId() != null) {
            builder.setProductVariantId(StringValue.of(dto.getProductVariantId().toString()));
        }
        if (dto.getProductVariantName() != null) {
            builder.setProductVariantName(StringValue.of(dto.getProductVariantName()));
        }

        // Set numeric values
        if (dto.getInsurancePrice() != null) {
            builder.setInsurancePrice(com.google.protobuf.DoubleValue.of(dto.getInsurancePrice().doubleValue()));
        }
        if (dto.getClientContributionPercentage() != null) {
            builder.setClientContributionPercentage(com.google.protobuf.DoubleValue.of(dto.getClientContributionPercentage().doubleValue()));
        }
        if (dto.getInsuranceCoveragePercentage() != null) {
            builder.setInsuranceCoveragePercentage(com.google.protobuf.DoubleValue.of(dto.getInsuranceCoveragePercentage().doubleValue()));
        }
        if (dto.getMaxCoverageAmount() != null) {
            builder.setMaxCoverageAmount(com.google.protobuf.DoubleValue.of(dto.getMaxCoverageAmount().doubleValue()));
        }
        if (dto.getMinClientContribution() != null) {
            builder.setMinClientContribution(com.google.protobuf.DoubleValue.of(dto.getMinClientContribution().doubleValue()));
        }
        if (dto.getMaxClientContribution() != null) {
            builder.setMaxClientContribution(com.google.protobuf.DoubleValue.of(dto.getMaxClientContribution().doubleValue()));
        }

        // Set boolean fields
        if (dto.getRequiresPreApproval() != null) {
            builder.setRequiresPreApproval(com.google.protobuf.BoolValue.of(dto.getRequiresPreApproval()));
        }

        // Set string fields
        if (dto.getConditions() != null) {
            builder.setConditions(StringValue.of(dto.getConditions()));
        }
        if (dto.getApprovalNotes() != null) {
            builder.setApprovalNotes(StringValue.of(dto.getApprovalNotes()));
        }

        // Set date fields
        if (dto.getEffectiveFrom() != null) {
            builder.setEffectiveFrom(toGrpcTimestamp(dto.getEffectiveFrom()));
        }
        if (dto.getEffectiveTo() != null) {
            builder.setEffectiveTo(toGrpcTimestamp(dto.getEffectiveTo()));
        }
        if (dto.getCreatedAt() != null) {
            builder.setCreatedAt(toGrpcTimestamp(dto.getCreatedAt()));
        }
        if (dto.getUpdatedAt() != null) {
            builder.setUpdatedAt(toGrpcTimestamp(dto.getUpdatedAt()));
        }

        // Set user tracking fields
        if (dto.getCreatedBy() != null) {
            builder.setCreatedBy(StringValue.of(dto.getCreatedBy()));
        }
        if (dto.getUpdatedBy() != null) {
            builder.setUpdatedBy(StringValue.of(dto.getUpdatedBy()));
        }

        return builder.build();
    }

    private GrpcCategoryDetails mapCategory(CategoryDto dto) {
        if (dto == null) {
            return null;
        }

        GrpcCategoryDetails.Builder builder = GrpcCategoryDetails.newBuilder()
                .setId(dto.getId().toString())
                .setName(dto.getName())
                .setCode(dto.getCode())
                .setIsActive(dto.getIsActive());

        if (dto.getDescription() != null) {
            builder.setDescription(StringValue.of(dto.getDescription()));
        }
        if (dto.getLevel() != null) {
            builder.setLevel(com.google.protobuf.Int32Value.of(dto.getLevel()));
        }
        if (dto.getParentId() != null) {
            builder.setParentId(StringValue.of(dto.getParentId().toString()));
        }
        if (dto.getDisplayOrder() != null) {
            builder.setDisplayOrder(com.google.protobuf.Int32Value.of(dto.getDisplayOrder()));
        }
        if (dto.getIcon() != null) {
            builder.setIcon(StringValue.of(dto.getIcon()));
        }
        if (dto.getTaxCategory() != null) {
            builder.setTaxCategory(StringValue.of(dto.getTaxCategory()));
        }
        if (dto.getRegulatoryCategory() != null) {
            builder.setRegulatoryCategory(StringValue.of(dto.getRegulatoryCategory()));
        }

        return builder.build();
    }

    private Timestamp toGrpcTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }

        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    private GrpcProductStatus mapProductStatus(ProductStatus status) {
        if (status == null) {
            return GrpcProductStatus.DRAFT;
        }

        return switch (status) {
            case ACTIVE -> GrpcProductStatus.ACTIVE;
            case SEASONAL -> GrpcProductStatus.SEASONAL;
            case DISCONTINUED -> GrpcProductStatus.DISCONTINUED;
            case RECALL -> GrpcProductStatus.RECALL;
            case PENDING_APPROVAL -> GrpcProductStatus.PENDING_APPROVAL;
            default -> GrpcProductStatus.DRAFT;
        };
    }

    private GrpcLifecycleStage mapLifecycleStage(com.nexxserve.catalog.enums.LifecycleStage stage) {
        if (stage == null) {
            return GrpcLifecycleStage.LIFECYCLE_NEW;
        }

        return switch (stage) {
            case GROWING -> GrpcLifecycleStage.LIFECYCLE_GROWING;
            case MATURE -> GrpcLifecycleStage.LIFECYCLE_MATURE;
            case DECLINING -> GrpcLifecycleStage.LIFECYCLE_DECLINING;
            case DISCONTINUED -> GrpcLifecycleStage.LIFECYCLE_DISCONTINUED;
            default -> GrpcLifecycleStage.LIFECYCLE_NEW;
        };
    }

    private GrpcHazardClass mapHazardClass(com.nexxserve.catalog.enums.HazardClass hazardClass) {
        if (hazardClass == null) {
            return GrpcHazardClass.NONE;
        }

        return switch (hazardClass) {
            case FLAMMABLE -> GrpcHazardClass.FLAMMABLE;
            case CORROSIVE -> GrpcHazardClass.CORROSIVE;
            case TOXIC -> GrpcHazardClass.TOXIC;
            case EXPLOSIVE -> GrpcHazardClass.EXPLOSIVE;
            default -> GrpcHazardClass.NONE;
        };
    }

    private GrpcUnitOfMeasure mapUnitOfMeasure(com.nexxserve.catalog.enums.UnitOfMeasure unitOfMeasure) {
        if (unitOfMeasure == null) {
            return GrpcUnitOfMeasure.PIECE;
        }

        return switch (unitOfMeasure) {
            case KILOGRAM -> GrpcUnitOfMeasure.KILOGRAM;
            case GRAM -> GrpcUnitOfMeasure.GRAM;
            case LITER -> GrpcUnitOfMeasure.LITER;
            case MILLILITER -> GrpcUnitOfMeasure.MILLILITER;
            case METER -> GrpcUnitOfMeasure.METER;
            case CENTIMETER -> GrpcUnitOfMeasure.CENTIMETER;
            case INCH -> GrpcUnitOfMeasure.INCH;
            case POUND -> GrpcUnitOfMeasure.POUND;
            case OUNCE -> GrpcUnitOfMeasure.OUNCE;
            default -> GrpcUnitOfMeasure.PIECE;
        };
    }

    private GrpcCoverageStatus mapCoverageStatus(com.nexxserve.catalog.enums.CoverageStatus status) {
        if (status == null) {
            return GrpcCoverageStatus.COVERAGE_ACTIVE;
        }

        return switch (status) {
            case INACTIVE -> GrpcCoverageStatus.COVERAGE_INACTIVE;
            case PENDING_APPROVAL -> GrpcCoverageStatus.COVERAGE_PENDING_APPROVAL;
            case SUSPENDED -> GrpcCoverageStatus.COVERAGE_SUSPENDED;
            case EXPIRED -> GrpcCoverageStatus.COVERAGE_EXPIRED;
            default -> GrpcCoverageStatus.COVERAGE_ACTIVE;
        };
    }

    private GrpcApprovalType mapApprovalType(com.nexxserve.catalog.enums.ApprovalType approvalType) {
        if (approvalType == null) {
            return GrpcApprovalType.AUTOMATIC;
        }

        return switch (approvalType) {
            case MANUAL_APPROVAL -> GrpcApprovalType.MANUAL_APPROVAL;
            case CONDITIONAL -> GrpcApprovalType.CONDITIONAL;
            case PRE_AUTHORIZATION  -> GrpcApprovalType.PRE_APPROVAL;
            default -> GrpcApprovalType.AUTOMATIC;
        };
    }
}