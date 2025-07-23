//package com.nexxserve.inventoryservice.grpc.client;
//
//import com.nexxserve.inventoryservice.exception.MedicineClientException;
//import com.nexxserve.medicine.grpc.MedicineServiceGrpc;
//import com.nexxserve.medicine.grpc.MedicineProto.MedicineReferenceRequest;
//import com.nexxserve.medicine.grpc.MedicineProto.MedicineResponse;
//import com.nexxserve.medicine.grpc.MedicineProto.ProductType;
//import io.grpc.ManagedChannel;
//import io.grpc.ManagedChannelBuilder;
//import io.grpc.StatusRuntimeException;
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PreDestroy;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.stereotype.Service;
//import io.grpc.Status;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
//
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//
//@Slf4j
//@RequiredArgsConstructor
//@Service
//public class MedicineGrpcClient {
//
//    private final DiscoveryClient discoveryClient;
//    private static final String SERVICE_NAME = "medicine";
//
//    @Value("${grpc.client.medicine.host:localhost}")
//    private String host;
//
//    @Value("${grpc.client.medicine.port:9090}")
//    private int port;
//
//    @Value("${grpc.client.medicine.timeout:30}")
//    private long timeoutSeconds;
//
//    private ManagedChannel channel;
//
//    @PostConstruct
//   public void init() {
//       log.info("Initializing gRPC client for Medicine service at {}:{}", host, port);
//       try {
//           List<ServiceInstance> instances = discoveryClient.getInstances(SERVICE_NAME);
//           if (instances == null || instances.isEmpty()) {
//                log.warn("No instances found for service: {}", SERVICE_NAME);
//              } else {
//                // Use the first available instance
//                ServiceInstance instance = instances.getFirst();
//                host = instance.getHost();
//                port = Integer.parseInt(instance.getMetadata().get("grpc-port"));
//                log.info("Using discovered service instance: {}:{}", host, port);
//           }
//       } catch (StatusRuntimeException e) {
//           throw new MedicineClientException("Failed to discover service instances for " + SERVICE_NAME, e);
//       }
//
//       channel = ManagedChannelBuilder.forAddress(host, port)
//               .usePlaintext()
//               .build();
//
//       // Create the blocking stub without a deadline
//        MedicineServiceGrpc.MedicineServiceBlockingStub blockingStub = MedicineServiceGrpc.newBlockingStub(channel);
//
//       log.info("Medicine gRPC client initialized successfully");
//   }
//
//    @PreDestroy
//    public void cleanup() {
//        log.info("Shutting down Medicine gRPC client");
//        if (channel != null && !channel.isShutdown()) {
//            try {
//                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                log.warn("Interrupted while shutting down gRPC channel", e);
//            }
//        }
//    }
//
//    /**
//     * Fetch medicine data by reference ID and product type
//     *
//     * @param referenceId The reference ID of the medicine
//     * @param productType The type of product (GENERIC, BRAND, or VARIANT)
//     * @return MedicineResponse containing the medicine details
//     * @throws MedicineClientException if the request fails
//     */
//    public MedicineResponse getMedicineByReference(String referenceId, ProductType productType) {
//        log.debug("Fetching medicine data for reference: {} and type: {}", referenceId, productType);
//
//        if (referenceId == null || referenceId.trim().isEmpty()) {
//            throw new IllegalArgumentException("Reference ID cannot be null or empty");
//        }
//
//        if (productType == null) {
//            throw new IllegalArgumentException("Product type cannot be null");
//        }
//
//        try {
//            // Validate UUID format before making the gRPC call
//            try {
//                UUID.fromString(referenceId);
//            } catch (IllegalArgumentException e) {
//                log.warn("Invalid UUID format for reference ID: {}", referenceId);
//                throw new MedicineClientException("Invalid UUID format: " + referenceId, e);
//            }
//
//            MedicineReferenceRequest request = MedicineReferenceRequest.newBuilder()
//                    .setReferenceId(referenceId)
//                    .setProductType(productType)
//                    .build();
//
//            // Refresh the blocking stub with a new deadline before each call
//            MedicineServiceGrpc.MedicineServiceBlockingStub callStub =
//                MedicineServiceGrpc.newBlockingStub(channel)
//                    .withDeadlineAfter(timeoutSeconds, TimeUnit.SECONDS);
//
//            MedicineResponse response = callStub.getMedicineByReference(request);
//
//            if (response == null || response.getId().isEmpty()) {
//                log.warn("No medicine data found for reference: {} and type: {}", referenceId, productType);
//                throw new MedicineClientException("Medicine not found with id: " + referenceId);
//            }
//
//            log.debug("Successfully retrieved medicine data for reference: {}", referenceId);
//            return response;
//
//        } catch (StatusRuntimeException e) {
//            log.error("gRPC call failed for reference: {} with status: {}", referenceId, e.getStatus(), e);
//
//            // More specific error handling based on status code
//            if (e.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED) {
//                throw new MedicineClientException("Request timed out while fetching medicine data", e);
//            } else if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
//                throw new MedicineClientException("Medicine not found with id: " + referenceId, e);
//            } else if (e.getStatus().getCode() == Status.Code.INVALID_ARGUMENT) {
//                throw new MedicineClientException("Invalid medicine reference: " + referenceId, e);
//            } else {
//                throw new MedicineClientException("Failed to fetch medicine data: " + e.getStatus().getDescription(), e);
//            }
//        } catch (Exception e) {
//            log.error("Unexpected error while fetching medicine data for reference: {}", referenceId, e);
//            throw new MedicineClientException("Unexpected error occurred while fetching medicine data", e);
//        }
//    }
//
//    /**
//     * Convenience method for fetching generic medicine
//     */
//    public MedicineResponse getGenericMedicine(String referenceId) {
//        return getMedicineByReference(referenceId, ProductType.MEDICINE_GENERIC);
//    }
//
//    /**
//     * Convenience method for fetching brand medicine
//     */
//    public MedicineResponse getBrandMedicine(String referenceId) {
//        return getMedicineByReference(referenceId, ProductType.MEDICINE_BRAND);
//    }
//
//    /**
//     * Convenience method for fetching variant medicine
//     */
//    public MedicineResponse getVariantMedicine(String referenceId) {
//        return getMedicineByReference(referenceId, ProductType.MEDICINE_VARIANT);
//    }
//
//    /**
//     * Check if the gRPC channel is ready for communication
//     */
//    public boolean isChannelReady() {
//        return channel != null && !channel.isShutdown() && !channel.isTerminated();
//    }
//}