package com.nexxserve.inventoryservice.service;

import com.nexxserve.catalog.grpc.GrpcProductType;
import com.nexxserve.inventoryservice.dto.admin.InventoryReportDTO;
import com.nexxserve.inventoryservice.dto.stock.CreateStockEntryRequest;
import com.nexxserve.inventoryservice.dto.stock.StockAlertResponse;
import com.nexxserve.inventoryservice.dto.stock.StockEntryResponse;
import com.nexxserve.inventoryservice.entity.inventory.StockAlert;
import com.nexxserve.inventoryservice.entity.inventory.StockEntry;
import com.nexxserve.inventoryservice.enums.AlertStatus;
import com.nexxserve.inventoryservice.enums.AlertType;
import com.nexxserve.inventoryservice.enums.ProductType;
import com.nexxserve.inventoryservice.enums.SourceService;
import com.nexxserve.inventoryservice.exception.MedicineClientException;
import com.nexxserve.inventoryservice.exception.ResourceNotFoundException;
//import com.nexxserve.inventoryservice.grpc.client.MedicineGrpcClient;
//import com.nexxserve.inventoryservice.grpc.client.ProductGrpcClient;
import com.nexxserve.inventoryservice.mapper.StockAlertMapper;
import com.nexxserve.inventoryservice.mapper.StockEntryMapper;
import com.nexxserve.inventoryservice.repository.StockAlertRepository;
import com.nexxserve.inventoryservice.repository.StockEntryRepository;
import com.nexxserve.inventoryservice.service.admin.RemoteReportService;
import com.nexxserve.medicine.grpc.MedicineProto;
import com.nexxserve.inventoryservice.dto.ProductNameInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nexxserve.inventoryservice.exception.MedicineValidationException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private final StockEntryRepository stockEntryRepository;
    private final StockAlertRepository stockAlertRepository;
    private final StockEntryMapper stockEntryMapper;
    private final StockAlertMapper stockAlertMapper;
//    private final MedicineGrpcClient medicineGrpcClient;
//    private final ProductGrpcClient productGrpcClient;
    private final ProductEnrichmentService productEnrichmentService;
    private final UserService userService;
    private final RemoteReportService remoteReportService;


    // Default threshold for low stock alerts
    private static final int DEFAULT_LOW_STOCK_THRESHOLD = 10;
    // Default days ahead for expiring soon alerts
    private static final int DEFAULT_EXPIRING_SOON_DAYS = 30;

    @Transactional
    public StockEntryResponse createStock(CreateStockEntryRequest request) {
//        // Remove the entire try-catch block to allow exceptions to propagate
//        if (request.getProductReference().getSourceService() == SourceService.MEDICINE) {
//            validateMedicineExists(request);
//        } else if (request.getProductReference().getSourceService() == SourceService.CATALOG) {
//            validateProductExists(request);
//        } else {
//            throw new IllegalArgumentException("Unsupported source service: " + request.getProductReference().getSourceService());
//        }
        ProductNameInfo productNameInfo = productEnrichmentService.extractProductNameInfo(request.getProductReference());


        StockEntry stockEntry = stockEntryMapper.toEntity(request);

        if (productNameInfo != null) {
            stockEntry.setProductName(productNameInfo.getName());
            stockEntry.setProductDescription(productNameInfo.getDescription());
        } else {
            throw new IllegalArgumentException("Unsupported product name: " + request.getProductReference().getSourceService());
        }
        // Save stock entry
        stockEntry.setCreatedBy(userService.getCurrentUserId().toString());
        StockEntry savedEntry = stockEntryRepository.save(stockEntry);

        // Check if alerts need to be generated
        checkAndGenerateAlerts(savedEntry);
        try {
            InventoryReportDTO dto = toInventoryReportDTO(savedEntry);
            remoteReportService.sendInventoryReport(dto);
            log.info("Sent inventory report for stock entry {}", savedEntry.getId());
        } catch (Exception e) {
            log.error("Failed to send inventory report for stock entry {}: {}", savedEntry.getId(), e.getMessage());
        }

        // Return response
        StockEntryResponse response = stockEntryMapper.toResponse(savedEntry);
        return productEnrichmentService.enrichStockEntryWithProductData(response);
    }

    private InventoryReportDTO toInventoryReportDTO(StockEntry entry) {
        InventoryReportDTO dto = new InventoryReportDTO();
        dto.setTransactionId(entry.getId().toString());
        dto.setProductType(entry.getProductType());
        dto.setProductId(entry.getReferenceId());
        dto.setAction("ADDITION");
        dto.setQuantity(entry.getQuantity());
        dto.setBuyingPrice(entry.getCostPrice().doubleValue());
        dto.setSellingPrice(entry.getUnitPrice().doubleValue());
        dto.setSupplierName(entry.getSupplierName());
        dto.setDoneBy(entry.getCreatedBy());
        dto.setDoneAt(entry.getCreatedAtAsLocalDateTime());
        dto.setExpirationDate(entry.getExpirationDate());
        // Set other fields as needed
        return dto;
    }


    private GrpcProductType mapToGrpcProductType(ProductType productType) {
        return switch (productType) {
            case CATALOG_PRODUCT_FAMILY -> GrpcProductType.CATALOG_PRODUCT_FAMILY;
            case CATALOG_PRODUCT_VARIANT -> GrpcProductType.CATALOG_PRODUCT_VARIANT;
            default -> throw new IllegalArgumentException("Unsupported product type: " + productType);
        };
    }


    /**
     * Validates that the medicine exists in the medicine service via gRPC
     *
     * @param request the stock entry request
     * @throws MedicineValidationException if the medicine does not exist
     * @throws MedicineClientException     if there's an error communicating with the medicine service
     */
//    private void validateMedicineExists(CreateStockEntryRequest request) {
//        String referenceId = request.getProductReference().getReferenceId();
//        ProductType productType = request.getProductReference().getProductType();
//
//        if (!medicineGrpcClient.isChannelReady()) {
//            throw new MedicineClientException("Medicine service is not available");
//        }
//
//        try {
//            MedicineProto.ProductType grpcProductType = mapToGrpcMedicineType(productType);
//            MedicineProto.MedicineResponse response =
//                    medicineGrpcClient.getMedicineByReference(referenceId, grpcProductType);
//
//            if (response == null || response.getId().isEmpty()) {
//                throw new MedicineValidationException(
//                        "Medicine with reference ID " + referenceId + " and type " + productType + " does not exist"
//                );
//            }
//
////            log.info("Validated medicine: {}, type: {}", referenceId, productType);
//        } catch (MedicineClientException e) {
//            throw e; // Re-throw client exceptions
//        } catch (Exception e) {
//            throw new MedicineValidationException("Failed to validate medicine: " + e.getMessage(), e);
//        }
//    }

    /**
     * Maps the internal ProductType to the gRPC ProductType
     */
    private MedicineProto.ProductType mapToGrpcMedicineType(ProductType productType) {
        return switch (productType) {
            case MEDICINE_GENERIC -> MedicineProto.ProductType.MEDICINE_GENERIC;
            case MEDICINE_BRAND -> MedicineProto.ProductType.MEDICINE_BRAND;
            case MEDICINE_VARIANT -> MedicineProto.ProductType.MEDICINE_VARIANT;
            default -> throw new IllegalArgumentException("Unsupported product type: " + productType);
        };
    }

    @Transactional(readOnly = true)
    public StockEntryResponse getStockById(UUID id) {
        StockEntry stockEntry = findStockEntryById(id);
        StockEntryResponse response = stockEntryMapper.toResponse(stockEntry);
        return productEnrichmentService.enrichStockEntryWithProductData(response);
    }

    @Transactional(readOnly = true)
    public Page<StockEntryResponse> getAllStocks(Pageable pageable) {
        return stockEntryRepository.findAll(pageable)
                .map(stockEntryMapper::toResponse)
                .map(productEnrichmentService::enrichStockEntryWithProductData);
    }

    @Transactional(readOnly = true)
    public Page<StockEntryResponse> getStocksByProductTypeAndSourceService(
            ProductType productType, SourceService sourceService, Pageable pageable) {
        return stockEntryRepository.findByProductTypeAndSourceService(productType, sourceService, pageable)
                .map(stockEntryMapper::toResponse)
                .map(productEnrichmentService::enrichStockEntryWithProductData);
    }

    @Transactional
    public StockEntryResponse updateStock(UUID id, CreateStockEntryRequest request) {
        StockEntry stockEntry = findStockEntryById(id);
        stockEntryMapper.updateEntityFromRequest(stockEntry, request);

        StockEntry updatedEntry = stockEntryRepository.save(stockEntry);

        // Check if alerts need to be updated or generated
        checkAndGenerateAlerts(updatedEntry);

        return stockEntryMapper.toResponse(updatedEntry);
    }

    @Transactional
    public void deleteStock(UUID id) {
        StockEntry stockEntry = findStockEntryById(id);

        // Delete associated alerts first
        List<StockAlert> alerts = stockAlertRepository.findByStockEntry(stockEntry);
        stockAlertRepository.deleteAll(alerts);

        // Delete stock entry
        stockEntryRepository.delete(stockEntry);
    }

    @Transactional
    public StockEntryResponse adjustStockQuantity(UUID id, int quantityChange) {
        StockEntry stockEntry = findStockEntryById(id);

        if (quantityChange > 0) {
            stockEntry.increaseQuantity(quantityChange);
        } else if (quantityChange < 0) {
            stockEntry.reduceQuantity(Math.abs(quantityChange));
        }

        StockEntry savedEntry = stockEntryRepository.save(stockEntry);

        // Check if alerts need to be generated or updated
        checkAndGenerateAlerts(savedEntry);

        return stockEntryMapper.toResponse(savedEntry);
    }

    // Alert related methods

    @Transactional(readOnly = true)
    public List<StockAlertResponse> getAlertsByStockId(UUID stockId) {
        StockEntry stockEntry = findStockEntryById(stockId);
        List<StockAlert> alerts = stockAlertRepository.findByStockEntry(stockEntry);
        return alerts.stream()
                .map(stockAlertMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<StockAlertResponse> getAllActiveAlerts(Pageable pageable) {
        return stockAlertRepository.findByStatus(AlertStatus.ACTIVE, pageable)
                .map(stockAlertMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<StockAlertResponse> getAlertsByType(AlertType alertType) {
        return stockAlertRepository.findByAlertType(alertType).stream()
                .map(stockAlertMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public StockAlertResponse resolveAlert(UUID alertId) {
        StockAlert alert = stockAlertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with id: " + alertId));

        alert.setStatus(AlertStatus.RESOLVED);
        StockAlert savedAlert = stockAlertRepository.save(alert);

        return stockAlertMapper.toResponse(savedAlert);
    }

    // Helper methods

    private StockEntry findStockEntryById(UUID id) {
        return stockEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock entry not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<StockEntryResponse> searchStocksByProductName(String searchTerm, Pageable pageable) {
        return stockEntryRepository.searchByProductName(searchTerm, pageable)
                .map(stockEntryMapper::toResponse)
                .map(productEnrichmentService::enrichStockEntryWithProductData);
    }

    private void checkAndGenerateAlerts(StockEntry stockEntry) {
        // Check for low stock situation
        if (stockEntry.getQuantity() <= DEFAULT_LOW_STOCK_THRESHOLD) {
            // Check if there's an existing active low stock alert
            List<StockAlert> existingAlerts = stockAlertRepository.findByStockEntryAndAlertTypeAndStatus(
                    stockEntry, AlertType.LOW_STOCK, AlertStatus.ACTIVE);

            if (existingAlerts.isEmpty()) {
                // Create new low stock alert
                StockAlert alert = stockAlertMapper.createLowStockAlert(stockEntry, DEFAULT_LOW_STOCK_THRESHOLD);
                stockAlertRepository.save(alert);
            }
        }

        // Check for expiring soon situation
        if (stockEntry.isExpiringSoon(DEFAULT_EXPIRING_SOON_DAYS) && !stockEntry.isExpired()) {
            List<StockAlert> existingAlerts = stockAlertRepository.findByStockEntryAndAlertTypeAndStatus(
                    stockEntry, AlertType.EXPIRING_SOON, AlertStatus.ACTIVE);

            if (existingAlerts.isEmpty()) {
                // Create new expiring soon alert
                StockAlert alert = stockAlertMapper.createExpiringSoonAlert(stockEntry, DEFAULT_EXPIRING_SOON_DAYS);
                stockAlertRepository.save(alert);
            }
        }

        // Check for expired situation
        if (stockEntry.isExpired()) {
            List<StockAlert> existingAlerts = stockAlertRepository.findByStockEntryAndAlertTypeAndStatus(
                    stockEntry, AlertType.EXPIRED, AlertStatus.ACTIVE);

            if (existingAlerts.isEmpty()) {
                // Create new expired alert
                StockAlert alert = stockAlertMapper.createExpiredAlert(stockEntry);
                stockAlertRepository.save(alert);
            }
        }
    }
}