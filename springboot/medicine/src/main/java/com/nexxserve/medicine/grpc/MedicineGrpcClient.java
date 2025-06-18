package com.nexxserve.medicine.grpc;

import com.nexxserve.medicine.grpc.MedicineProto.*;
import com.nexxserve.medicine.grpc.MedicineServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MedicineGrpcClient {

    private ManagedChannel channel;
    private MedicineServiceGrpc.MedicineServiceBlockingStub blockingStub;

    @PostConstruct
    public void init() {
        // Configure the channel - adjust host and port as needed
        // For local development, you might want to make this configurable
        String host = System.getProperty("grpc.client.host", "localhost");
        int port = Integer.parseInt(System.getProperty("grpc.client.port", "9080"));

        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();

        blockingStub = MedicineServiceGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        if (channel != null) {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    /**
     * Get medicine data by reference ID and product type
     */
    public MedicineResponse getMedicineByReference(String referenceId, ProductType productType) {
        try {
            MedicineReferenceRequest request = MedicineReferenceRequest.newBuilder()
                    .setReferenceId(referenceId)
                    .setProductType(productType)
                    .build();

            log.info("Sending gRPC request for referenceId: {} with productType: {}",
                    referenceId, productType);

            MedicineResponse response = blockingStub.getMedicineByReference(request);

            log.info("Received response for medicine: {} (Type: {})",
                    response.getName(), response.getProductType());

            return response;

        } catch (Exception e) {
            log.error("Error calling gRPC service: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get medicine data", e);
        }
    }

    /**
     * Convenience method to get generic medicine
     */
    public MedicineResponse getGeneric(String referenceId) {
        return getMedicineByReference(referenceId, ProductType.MEDICINE_GENERIC);
    }

    /**
     * Convenience method to get brand medicine
     */
    public MedicineResponse getBrand(String referenceId) {
        return getMedicineByReference(referenceId, ProductType.MEDICINE_BRAND);
    }

    /**
     * Convenience method to get variant medicine
     */
    public MedicineResponse getVariant(String referenceId) {
        return getMedicineByReference(referenceId, ProductType.MEDICINE_VARIANT);
    }

    /**
     * Example usage method
     */
    public void demonstrateUsage() {
        try {
            // Example UUIDs - replace with actual IDs from your database
            String genericId = "123e4567-e89b-12d3-a456-426614174000";
            String brandId = "123e4567-e89b-12d3-a456-426614174001";
            String variantId = "123e4567-e89b-12d3-a456-426614174002";

            // Get generic medicine
            MedicineResponse genericResponse = getGeneric(genericId);
            log.info("Generic Medicine: {}", genericResponse.getName());
            if (genericResponse.hasGenericDetails()) {
                log.info("Chemical Name: {}", genericResponse.getGenericDetails().getChemicalName());
                log.info("Is Parent: {}", genericResponse.getGenericDetails().getIsParent());
            }

            // Get brand medicine
            MedicineResponse brandResponse = getBrand(brandId);
            log.info("Brand Medicine: {}", brandResponse.getName());
            if (brandResponse.hasBrandDetails()) {
                log.info("Manufacturer: {}", brandResponse.getBrandDetails().getManufacturer());
                log.info("Country: {}", brandResponse.getBrandDetails().getCountry());
            }

            // Get variant medicine
            MedicineResponse variantResponse = getVariant(variantId);
            log.info("Variant Medicine: {}", variantResponse.getName());
            if (variantResponse.hasVariantDetails()) {
                log.info("Form: {}", variantResponse.getVariantDetails().getForm());
                log.info("Strength: {}", variantResponse.getVariantDetails().getStrength());
                log.info("Generic Count: {}", variantResponse.getVariantDetails().getGenericsCount());
            }

            // Print insurance coverages for any response
            printInsuranceCoverages(genericResponse);

        } catch (Exception e) {
            log.error("Error demonstrating usage: {}", e.getMessage(), e);
        }
    }

    private void printInsuranceCoverages(MedicineResponse response) {
        if (response.getInsuranceCoveragesCount() > 0) {
            log.info("Insurance Coverages:");
            response.getInsuranceCoveragesList().forEach(coverage -> {
                log.info("  - {}: {}% coverage, Max: {}",
                        coverage.getInsuranceName(),
                        coverage.getCoveragePercentage(),
                        coverage.getMaxAmount());
            });
        }
    }
}