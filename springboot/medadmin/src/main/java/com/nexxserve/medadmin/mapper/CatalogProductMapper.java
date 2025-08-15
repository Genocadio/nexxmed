package com.nexxserve.medadmin.mapper;//package com.nexxserve.inventoryservice.mapper;
//
//import com.nexxserve.catalog.grpc.GetProductResponse;
//import com.nexxserve.inventoryservice.dto.CatalogProductData;
//import com.nexxserve.inventoryservice.dto.Insurance.InsuranceCoverageDetails;
//import com.nexxserve.inventoryservice.dto.ProductFamilyDetails;
//import com.nexxserve.inventoryservice.dto.CategoryDetails;
//import org.springframework.stereotype.Component;
//import com.nexxserve.inventoryservice.dto.ProductVariantDetails;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Component
//public class CatalogProductMapper {
//
//    public CatalogProductData toCatalogProductData(GetProductResponse response) {
//        CatalogProductData data = new CatalogProductData();
//        data.setSuccess(response.getSuccess());
//        data.setErrorMessage(response.getErrorMessage());
//
//        // Map family or variant details based on which is present in the response
//        if (response.hasFamily()) {
//            data.setProductType("CATALOG_PRODUCT_FAMILY");
//            data.setFamilyDetails(mapProductFamily(response.getFamily()));
//        } else if (response.hasVariant()) {
//            data.setProductType("CATALOG_PRODUCT_VARIANT");
//            data.setVariantDetails(mapProductVariant(response.getVariant()));
//        }
//
//        return data;
//    }
//
//    private CategoryDetails mapCategory(com.nexxserve.catalog.grpc.GrpcCategoryDetails grpcCategory) {
//        CategoryDetails category = new CategoryDetails();
//
//        category.setId(grpcCategory.getId());
//        category.setName(grpcCategory.getName());
//        category.setCode(grpcCategory.getCode());
//        category.setActive(grpcCategory.getIsActive());
//
//        if (grpcCategory.hasDescription()) {
//            category.setDescription(grpcCategory.getDescription().getValue());
//        }
//
//        if (grpcCategory.hasLevel()) {
//            category.setLevel(grpcCategory.getLevel().getValue());
//        }
//
//        if (grpcCategory.hasParentId()) {
//            category.setParentId(grpcCategory.getParentId().getValue());
//        }
//
//        if (grpcCategory.hasDisplayOrder()) {
//            category.setDisplayOrder(grpcCategory.getDisplayOrder().getValue());
//        }
//
//        if (grpcCategory.hasIcon()) {
//            category.setIcon(grpcCategory.getIcon().getValue());
//        }
//
//        if (grpcCategory.hasTaxCategory()) {
//            category.setTaxCategory(grpcCategory.getTaxCategory().getValue());
//        }
//
//        if (grpcCategory.hasRegulatoryCategory()) {
//            category.setRegulatoryCategory(grpcCategory.getRegulatoryCategory().getValue());
//        }
//
//        return category;
//    }
//
//    private ProductFamilyDetails mapProductFamily(com.nexxserve.catalog.grpc.GrpcProductFamilyDetails family) {
//        ProductFamilyDetails details = new ProductFamilyDetails();
//
//        details.setId(family.getId());
//        details.setName(family.getName());
//
//        // Optional fields
//        if (family.hasDescription()) {
//            details.setDescription(family.getDescription().getValue());
//        }
//
//        if (family.hasShortDescription()) {
//            details.setShortDescription(family.getShortDescription().getValue());
//        }
//
//        // Category mapping
//        if (family.hasCategory()) {
//            details.setCategory(mapCategory(family.getCategory()));
//        }
//
//        // Lists
//        details.setSubCategoryIds(family.getSubCategoryIdsList());
//        details.setTags(family.getTagsList());
//        details.setCertifications(family.getCertificationsList());
//
//        if (family.hasSearchKeywords()) {
//            details.setSearchKeywords(family.getSearchKeywords().getValue());
//        }
//
//        if (family.hasBrand()) {
//            details.setBrand(family.getBrand().getValue());
//        }
//
//        // Status
//        details.setStatus(family.getStatus().name());
//
//        // Dates
//        if (family.hasLaunchDate()) {
//            details.setLaunchDate(convertTimestampToLocalDateTime(family.getLaunchDate()));
//        }
//
//        if (family.hasDiscontinueDate()) {
//            details.setDiscontinueDate(convertTimestampToLocalDateTime(family.getDiscontinueDate()));
//        }
//
//        details.setLifecycleStage(family.getLifecycleStage().name());
//
//        if (family.hasAgeRestricted()) {
//            details.setAgeRestricted(family.getAgeRestricted().getValue());
//        }
//
//        details.setHazardClass(family.getHazardClass().name());
//
//        // Audit fields
//        if (family.hasCreatedAt()) {
//            details.setCreatedAt(convertTimestampToLocalDateTime(family.getCreatedAt()));
//        }
//
//        if (family.hasUpdatedAt()) {
//            details.setUpdatedAt(convertTimestampToLocalDateTime(family.getUpdatedAt()));
//        }
//
//        if (family.hasCreatedBy()) {
//            details.setCreatedBy(family.getCreatedBy().getValue());
//        }
//
//        if (family.hasUpdatedBy()) {
//            details.setUpdatedBy(family.getUpdatedBy().getValue());
//        }
//
//        if (family.hasVersion()) {
//            details.setVersion(family.getVersion().getValue());
//        }
//
//        if(family.getInsuranceCoveragesCount() > 0) {
//            details.setInsuranceCoverages(mapInsuranceCoverages(family.getInsuranceCoveragesList()));
//        }
//        // Map insurance coverages if needed
//        // details.setInsuranceCoverages(mapInsuranceCoverages(family.getInsuranceCoveragesList()));
//
//        return details;
//    }
//
//
//
//
//    private List<InsuranceCoverageDetails> mapInsuranceCoverages(List<com.nexxserve.catalog.grpc.ProductInsuranceCoverageDetails> coverages) {
//        if (coverages == null || coverages.isEmpty()) {
//            return null;
//        }
//
//        return coverages.stream()
//                .map(this::mapInsuranceCoverage)
//                .collect(java.util.stream.Collectors.toList());
//    }
//
//    private InsuranceCoverageDetails mapInsuranceCoverage(com.nexxserve.catalog.grpc.ProductInsuranceCoverageDetails coverage) {
//        InsuranceCoverageDetails details = new InsuranceCoverageDetails();
//        details.setId(coverage.getId());
//        details.setInsuranceId(coverage.getInsuranceId());
//        details.setInsuranceName(coverage.getInsuranceName());
//
//        if (coverage.hasProductFamilyId()) {
//            details.setProductFamilyId(coverage.getProductFamilyId().getValue());
//        }
//
//        if (coverage.hasProductFamilyName()) {
//            details.setProductFamilyName(coverage.getProductFamilyName().getValue());
//        }
//
//        if (coverage.hasProductVariantId()) {
//            details.setProductVariantId(coverage.getProductVariantId().getValue());
//        }
//
//        if (coverage.hasProductVariantName()) {
//            details.setProductVariantName(coverage.getProductVariantName().getValue());
//        }
//
//        details.setStatus(coverage.getStatus().name());
//
//        if (coverage.hasInsurancePrice()) {
//            details.setInsurancePrice(coverage.getInsurancePrice().getValue());
//        }
//
//        if (coverage.hasClientContributionPercentage()) {
//            details.setClientContributionPercentage(coverage.getClientContributionPercentage().getValue());
//        }
//
//        if (coverage.hasInsuranceCoveragePercentage()) {
//            details.setInsuranceCoveragePercentage(coverage.getInsuranceCoveragePercentage().getValue());
//        }
//
//        if (coverage.hasRequiresPreApproval()) {
//            details.setRequiresPreApproval(coverage.getRequiresPreApproval().getValue());
//        }
//
//        details.setApprovalType(coverage.getApprovalType().name());
//
//        if (coverage.hasMaxCoverageAmount()) {
//            details.setMaxCoverageAmount(coverage.getMaxCoverageAmount().getValue());
//        }
//
//        if (coverage.hasMinClientContribution()) {
//            details.setMinClientContribution(coverage.getMinClientContribution().getValue());
//        }
//
//        if (coverage.hasMaxClientContribution()) {
//            details.setMaxClientContribution(coverage.getMaxClientContribution().getValue());
//        }
//
//        if (coverage.hasEffectiveFrom()) {
//            details.setEffectiveFrom(convertTimestampToLocalDateTime(coverage.getEffectiveFrom()));
//        }
//
//        if (coverage.hasEffectiveTo()) {
//            details.setEffectiveTo(convertTimestampToLocalDateTime(coverage.getEffectiveTo()));
//        }
//
//        if (coverage.hasConditions()) {
//            details.setConditions(coverage.getConditions().getValue());
//        }
//
//        if (coverage.hasApprovalNotes()) {
//            details.setApprovalNotes(coverage.getApprovalNotes().getValue());
//        }
//
//        if (coverage.hasCreatedAt()) {
//            details.setCreatedAt(convertTimestampToLocalDateTime(coverage.getCreatedAt()));
//        }
//
//        if (coverage.hasUpdatedAt()) {
//            details.setUpdatedAt(convertTimestampToLocalDateTime(coverage.getUpdatedAt()));
//        }
//
//        if (coverage.hasCreatedBy()) {
//            details.setCreatedBy(coverage.getCreatedBy().getValue());
//        }
//
//        if (coverage.hasUpdatedBy()) {
//            details.setUpdatedBy(coverage.getUpdatedBy().getValue());
//        }
//
//        return details;
//    }
//
//    private LocalDateTime convertTimestampToLocalDateTime(com.google.protobuf.Timestamp timestamp) {
//        return java.time.LocalDateTime.ofInstant(
//                java.time.Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos()),
//                java.time.ZoneId.systemDefault());
//    }
//
//    private ProductVariantDetails mapProductVariant(com.nexxserve.catalog.grpc.ProductVariantDetails variant) {
//        ProductVariantDetails details = new ProductVariantDetails();
//
//        details.setId(variant.getId());
//        details.setName(variant.getName());
//        details.setSku(variant.getSku());
//        details.setBrand(variant.getBrand());
//
//        // Map the family details
//        if (variant.hasFamily()) {
//            details.setFamily(mapProductFamily(variant.getFamily()));
//        }
//
//        // Optional fields
//        if (variant.hasDisplayName()) {
//            details.setDisplayName(variant.getDisplayName().getValue());
//        }
//
//        if (variant.hasUpc()) {
//            details.setUpc(variant.getUpc().getValue());
//        }
//
//        if (variant.hasGtin()) {
//            details.setGtin(variant.getGtin().getValue());
//        }
//
//        details.setBarcodes(variant.getBarcodesList());
//
//        if (variant.hasManufacturer()) {
//            details.setManufacturer(variant.getManufacturer().getValue());
//        }
//
//        if (variant.hasManufacturerPartNumber()) {
//            details.setManufacturerPartNumber(variant.getManufacturerPartNumber().getValue());
//        }
//
//        if (variant.hasCountryOfOrigin()) {
//            details.setCountryOfOrigin(variant.getCountryOfOrigin().getValue());
//        }
//
//        details.setAttributes(variant.getAttributesMap());
//
//        if (variant.hasColor()) {
//            details.setColor(variant.getColor().getValue());
//        }
//
//        details.setMaterial(variant.getMaterialList());
//
//        details.setUnitOfMeasure(variant.getUnitOfMeasure().name());
//
//        if (variant.hasUnitsPerPackage()) {
//            details.setUnitsPerPackage(variant.getUnitsPerPackage().getValue());
//        }
//
//        details.setAllergens(variant.getAllergensList());
//        details.setIngredients(variant.getIngredientsList());
//        details.setWarnings(variant.getWarningsList());
//        details.setInstructions(variant.getInstructionsList());
//        details.setSpecifications(variant.getSpecificationsMap());
//        details.setCompatibility(variant.getCompatibilityList());
//
//        details.setStatus(variant.getStatus().name());
//
//        if (variant.hasIsLimitedEdition()) {
//            details.setIsLimitedEdition(variant.getIsLimitedEdition().getValue());
//        }
//
//        if (variant.hasSearchKeywords()) {
//            details.setSearchKeywords(variant.getSearchKeywords().getValue());
//        }
//
//        if (variant.hasSeoUrl()) {
//            details.setSeoUrl(variant.getSeoUrl().getValue());
//        }
//
//        if (variant.hasMetaDescription()) {
//            details.setMetaDescription(variant.getMetaDescription().getValue());
//        }
//
//        if (variant.hasAverageRating()) {
//            details.setAverageRating(variant.getAverageRating().getValue());
//        }
//
//        if (variant.hasReviewCount()) {
//            details.setReviewCount(variant.getReviewCount().getValue());
//        }
//
//        details.setQualityCertifications(variant.getQualityCertificationsList());
//
//        // Audit fields
//        if (variant.hasCreatedAt()) {
//            details.setCreatedAt(convertTimestampToLocalDateTime(variant.getCreatedAt()));
//        }
//
//        if (variant.hasUpdatedAt()) {
//            details.setUpdatedAt(convertTimestampToLocalDateTime(variant.getUpdatedAt()));
//        }
//
//        if (variant.hasCreatedBy()) {
//            details.setCreatedBy(variant.getCreatedBy().getValue());
//        }
//
//        if (variant.hasUpdatedBy()) {
//            details.setUpdatedBy(variant.getUpdatedBy().getValue());
//        }
//
//        if (variant.hasVersion()) {
//            details.setVersion(variant.getVersion().getValue());
//        }
//
//        if(variant.getInsuranceCoveragesCount() > 0) {
//            details.setInsuranceCoverages(mapInsuranceCoverages(variant.getInsuranceCoveragesList()));
//        }
//
//        return details;
//    }
//
//}