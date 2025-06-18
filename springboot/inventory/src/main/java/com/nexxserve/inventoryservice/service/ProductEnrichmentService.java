package com.nexxserve.inventoryservice.service;

import com.nexxserve.catalog.grpc.GetProductResponse;
import com.nexxserve.catalog.grpc.GrpcProductType;
import com.nexxserve.inventoryservice.dto.*;
import com.nexxserve.inventoryservice.entity.StockEntry;
import com.nexxserve.inventoryservice.enums.ProductType;
import com.nexxserve.inventoryservice.enums.SourceService;
import com.nexxserve.inventoryservice.grpc.client.MedicineGrpcClient;
import com.nexxserve.inventoryservice.grpc.client.ProductGrpcClient;
import com.nexxserve.inventoryservice.mapper.CatalogProductMapper;
import com.nexxserve.inventoryservice.mapper.MedicineMapper;
import com.nexxserve.inventoryservice.mapper.StockEntryMapper;
import com.nexxserve.medicine.grpc.MedicineProto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductEnrichmentService {

    private final MedicineGrpcClient medicineGrpcClient;
    private final ProductGrpcClient productGrpcClient;
    private final MedicineMapper medicineMapper;
    private final CatalogProductMapper catalogProductMapper;

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
                enrichWithMedicineData(reference, productData);
            } else if (reference.getSourceService() == SourceService.CATALOG) {
                enrichWithCatalogData(reference, productData);
            }
        } catch (Exception e) {
            log.error("Error enriching stock entry with product data", e);
        }

        stockEntryResponse.setProductData(productData);
        return stockEntryResponse;
    }

    private void enrichWithMedicineData(ProductReference reference, EnrichedProductData productData) {
        try {
            MedicineProto.ProductType grpcProductType = mapToGrpcMedicineType(reference.getProductType());
            MedicineProto.MedicineResponse medicineResponse =
                medicineGrpcClient.getMedicineByReference(reference.getReferenceId(), grpcProductType);

            if (medicineResponse != null && !medicineResponse.getId().isEmpty()) {
                MedicineData medicineData = medicineMapper.toMedicineData(medicineResponse);
                productData.setMedicineData(medicineData);
                log.debug("Successfully mapped medicine data for ID: {}", reference.getReferenceId());
            } else {
                log.warn("Medicine data could not be retrieved for ID: {}", reference.getReferenceId());
            }
        } catch (Exception e) {
            log.warn("Error retrieving medicine data for ID: {}: {}",
                    reference.getReferenceId(), e.getMessage());
            // Don't set medicineData - it will remain null in this case
        }
    }

    private void enrichWithCatalogData(ProductReference reference, EnrichedProductData productData) {
        GrpcProductType grpcProductType = mapToGrpcProductType(reference.getProductType());
        GetProductResponse productResponse =
            productGrpcClient.getProductByReference(reference.getReferenceId(), grpcProductType);
        System.out.println("Product Response: " + productResponse);

        CatalogProductData catalogProductData = catalogProductMapper.toCatalogProductData(productResponse);
        productData.setCatalogProductData(catalogProductData);
        System.out.println("Catalog Product Data: " + catalogProductData);
    }

    private MedicineProto.ProductType mapToGrpcMedicineType(ProductType productType) {
        return switch (productType) {
            case MEDICINE_GENERIC -> MedicineProto.ProductType.MEDICINE_GENERIC;
            case MEDICINE_BRAND -> MedicineProto.ProductType.MEDICINE_BRAND;
            case MEDICINE_VARIANT -> MedicineProto.ProductType.MEDICINE_VARIANT;
            default -> throw new IllegalArgumentException("Unsupported medicine product type: " + productType);
        };
    }

    private GrpcProductType mapToGrpcProductType(ProductType productType) {
        return switch (productType) {
            case CATALOG_PRODUCT_FAMILY -> GrpcProductType.CATALOG_PRODUCT_FAMILY;
            case CATALOG_PRODUCT_VARIANT -> GrpcProductType.CATALOG_PRODUCT_VARIANT;
            default -> throw new IllegalArgumentException("Unsupported catalog product type: " + productType);
        };
    }


    /**
     * Extract product name from either medicine or catalog response
     */
    public ProductNameInfo extractProductNameInfo(ProductReference reference) {
        try {
            if (reference.getSourceService() == SourceService.MEDICINE) {
                MedicineProto.ProductType grpcProductType = mapToGrpcMedicineType(reference.getProductType());
                MedicineProto.MedicineResponse medicineResponse =
                        medicineGrpcClient.getMedicineByReference(reference.getReferenceId(), grpcProductType);

                if (medicineResponse != null && !medicineResponse.getId().isEmpty()) {
                    if (reference.getProductType() == ProductType.MEDICINE_GENERIC) {
                        return new ProductNameInfo(
                                medicineResponse.getName(),
                                medicineResponse.getGenericDetails().getDescription()
                        );
                    } else if (reference.getProductType() == ProductType.MEDICINE_BRAND) {
                        return new ProductNameInfo(
                                medicineResponse.getName(),
                                medicineResponse.getBrandDetails().getBrandName()
                        );
                    } else if (reference.getProductType() == ProductType.MEDICINE_VARIANT) {
                        return new ProductNameInfo(
                                medicineResponse.getName(),
                                medicineResponse.getVariantDetails().getTradeName()
                        );
                    }
                    return new ProductNameInfo(
                            medicineResponse.getName(),
                            ""
                    );
                } else {
                    log.warn("Medicine response could not be retrieved for ID: {}", reference.getReferenceId());
                    throw new IllegalArgumentException("Unsupported source service: " + reference.getSourceService());
                }
            } else if (reference.getSourceService() == SourceService.CATALOG) {
                GrpcProductType grpcProductType = mapToGrpcProductType(reference.getProductType());
                GetProductResponse productResponse =
                        productGrpcClient.getProductByReference(reference.getReferenceId(), grpcProductType);

                if (productResponse != null) {
                    if(reference.getProductType() == ProductType.CATALOG_PRODUCT_FAMILY) {
                        return new ProductNameInfo(
                                productResponse.getFamily().getName(),
                                productResponse.getFamily().getDescription().toString()
                        );
                    } else {
                        return new ProductNameInfo(
                                productResponse.getVariant().getName(),
                               ""
                        );
                    }

                } else {
                    log.warn("Catalog product data could not be retrieved for ID: {}", reference.getReferenceId());
                    throw new IllegalArgumentException("Catalog product data could not be retrieved for ID: " + reference.getReferenceId());
                }
            }
            return null;
        } catch (Exception e) {
            log.warn("Error extracting product name for ID: {}: {}",
                    reference.getReferenceId(), e.getMessage());
            return null;
        }
    }
}