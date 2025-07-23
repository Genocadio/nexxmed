package com.nexxserve.inventoryservice.service;

import com.nexxserve.catalog.grpc.GrpcProductType;
import com.nexxserve.inventoryservice.dto.*;
import com.nexxserve.inventoryservice.dto.medicine.*;
import com.nexxserve.inventoryservice.dto.stock.EnrichedProductData;
import com.nexxserve.inventoryservice.dto.stock.MedicineData;
import com.nexxserve.inventoryservice.dto.stock.StockEntryResponse;
import com.nexxserve.inventoryservice.enums.ProductType;
import com.nexxserve.inventoryservice.enums.SourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductEnrichmentService {


    // Replace gRPC client with local services
    private final GenericService genericService;
    private final VariantService variantService;
    private final BrandService brandService;
    private final ProductFamilyService productFamilyService;
    private final ProductVariantService productVariantService;

    /**
     * Enriches a stock entry response with product data from appropriate service
     */
    public StockEntryResponse enrichStockEntryWithProductData(StockEntryResponse stockEntryResponse) {
        ProductReference reference = stockEntryResponse.getProductReference();

        if (reference == null) {
            log.warn("Cannot enrich stock entry: missing product reference");
            return stockEntryResponse;
        }

        EnrichedProductData productData = new EnrichedProductData();
        productData.setSourceService(reference.getSourceService());

        try {
            if (reference.getSourceService() == SourceService.MEDICINE) {
                enrichWithLocalMedicineData(reference, productData);
            } else if (reference.getSourceService() == SourceService.CATALOG) {
                enrichWithLocalCatalogData(reference, productData);
            }
        } catch (Exception e) {
            log.error("Error enriching stock entry with product data", e);
        }

        stockEntryResponse.setProductData(productData);
        return stockEntryResponse;
    }

    private void enrichWithLocalMedicineData(ProductReference reference, EnrichedProductData productData) {
        try {
            UUID referenceId = UUID.fromString(reference.getReferenceId());
           MedicineData medicineData = new MedicineData();

            switch (reference.getProductType()) {
                case MEDICINE_GENERIC -> {
                    GenericResponseDto generic = genericService.findById(referenceId);
                    medicineData.setId(generic.getId().toString());
                    medicineData.setName(generic.getName());
                    medicineData.setProductType("MEDICINE_GENERIC");
                    medicineData.setCreatedAt(generic.getCreatedAt());
                    medicineData.setUpdatedAt(generic.getUpdatedAt());

                    // Set generic details
                    medicineData.setGenericDetails(generic);
                }
                case MEDICINE_VARIANT -> {
                    VariantResponseDto variant = variantService.findById(referenceId);
                    medicineData.setId(variant.getId().toString());
                    medicineData.setName(variant.getName());
                    medicineData.setProductType("MEDICINE_VARIANT");
                    medicineData.setCreatedAt(variant.getCreatedAt());
                    medicineData.setUpdatedAt(variant.getUpdatedAt());

                    // Set variant details
                    medicineData.setVariantDetails(variant);
                }
                case MEDICINE_BRAND -> {
                    BrandResponseDto brand = brandService.findById(referenceId);
                    medicineData.setId(brand.getId().toString());
                    medicineData.setName(brand.getBrandName());
                    medicineData.setProductType("MEDICINE_BRAND");
                    medicineData.setCreatedAt(brand.getCreatedAt());
                    medicineData.setUpdatedAt(brand.getUpdatedAt());

                    // Set brand details
                    medicineData.setBrandDetails(brand);
                }
                default -> throw new IllegalArgumentException("Unsupported medicine product type: " + reference.getProductType());
            }

            productData.setMedicineData(medicineData);
            log.debug("Successfully enriched with local medicine data for ID: {}", reference.getReferenceId());
        } catch (Exception e) {
            log.warn("Error retrieving local medicine data for ID: {}: {}",
                    reference.getReferenceId(), e.getMessage());
        }
    }

    private void enrichWithLocalCatalogData(ProductReference reference, EnrichedProductData productData) {
        try {
            UUID referenceId = UUID.fromString(reference.getReferenceId());
            CatalogProductData catalogProductData = new CatalogProductData();

            if (reference.getProductType() == ProductType.CATALOG_PRODUCT_FAMILY) {
                // Fetch family from local service
                var family = productFamilyService.getProductFamilyById(referenceId);
                catalogProductData.setId(family.getId().toString());
                catalogProductData.setName(family.getName());
                catalogProductData.setProductType("CATALOG_PRODUCT_FAMILY");
                catalogProductData.setSuccess(true);
                catalogProductData.setProductFamily(family);
            } else if (reference.getProductType() == ProductType.CATALOG_PRODUCT_VARIANT) {
                // Fetch variant from local service
                var variant = productVariantService.getProductVariantById(referenceId);
                catalogProductData.setId(variant.getId().toString());
                catalogProductData.setName(variant.getName());
                catalogProductData.setProductType("CATALOG_PRODUCT_VARIANT");
                catalogProductData.setSuccess(true);
                catalogProductData.setProductVariant(variant);
            } else {
                throw new IllegalArgumentException("Unsupported catalog product type: " + reference.getProductType());
            }

            productData.setCatalogProductData(catalogProductData);
            log.debug("Successfully enriched with local catalog data for ID: {}", reference.getReferenceId());
        } catch (Exception e) {
            log.warn("Error retrieving local catalog data for ID: {}: {}",
                    reference.getReferenceId(), e.getMessage());

            // Create error response
            CatalogProductData errorData = new CatalogProductData();
            errorData.setSuccess(false);
            errorData.setErrorMessage("Failed to retrieve catalog data: " + e.getMessage());
            productData.setCatalogProductData(errorData);
        }
    }

    private GrpcProductType mapToGrpcProductType(ProductType productType) {
        return switch (productType) {
            case CATALOG_PRODUCT_FAMILY -> GrpcProductType.CATALOG_PRODUCT_FAMILY;
            case CATALOG_PRODUCT_VARIANT -> GrpcProductType.CATALOG_PRODUCT_VARIANT;
            default -> throw new IllegalArgumentException("Unsupported catalog product type: " + productType);
        };
    }

    /**
     * Extract product name from either local medicine or catalog response
     */
    public ProductNameInfo extractProductNameInfo(ProductReference reference) {
        try {
            if (reference.getSourceService() == SourceService.MEDICINE) {
                return extractLocalMedicineNameInfo(reference);
            } else if (reference.getSourceService() == SourceService.CATALOG) {
                return extractLocalCatalogNameInfo(reference);
            }
            return null;
        } catch (Exception e) {
            log.warn("Error extracting product name for ID: {}: {}",
                    reference.getReferenceId(), e.getMessage());
            return null;
        }
    }

    private ProductNameInfo extractLocalMedicineNameInfo(ProductReference reference) {
        UUID referenceId = UUID.fromString(reference.getReferenceId());

        return switch (reference.getProductType()) {
            case MEDICINE_GENERIC -> {
                GenericResponseDto generic = genericService.findById(referenceId);
                yield new ProductNameInfo(generic.getName(), generic.getDescription());
            }
            case MEDICINE_VARIANT -> {
                VariantResponseDto variant = variantService.findById(referenceId);
                yield new ProductNameInfo(variant.getName(), variant.getTradeName());
            }
            case MEDICINE_BRAND -> {
                BrandResponseDto brand = brandService.findById(referenceId);
                yield new ProductNameInfo(brand.getBrandName(), brand.getManufacturer());
            }
            default -> throw new IllegalArgumentException("Unsupported medicine product type: " + reference.getProductType());
        };
    }

    private ProductNameInfo extractLocalCatalogNameInfo(ProductReference reference) {
        UUID referenceId = UUID.fromString(reference.getReferenceId());

        try {
            if (reference.getProductType() == ProductType.CATALOG_PRODUCT_FAMILY) {
                var family = productFamilyService.getProductFamilyById(referenceId);
                return new ProductNameInfo(
                        family.getName(),
                        family.getDescription() != null ? family.getDescription() : ""
                );
            } else if (reference.getProductType() == ProductType.CATALOG_PRODUCT_VARIANT) {
                var variant = productVariantService.getProductVariantById(referenceId);
                return new ProductNameInfo(
                        variant.getName(),
                        variant.getFamily().getDescription() != null ? variant.getFamily().getDescription() : ""
                );
            } else {
                throw new IllegalArgumentException("Unsupported catalog product type: " + reference.getProductType());
            }
        } catch (Exception e) {
            log.warn("Catalog product data could not be retrieved for ID: {}", reference.getReferenceId());
            throw new IllegalArgumentException("Catalog product data could not be retrieved for ID: " + reference.getReferenceId());
        }
    }
}