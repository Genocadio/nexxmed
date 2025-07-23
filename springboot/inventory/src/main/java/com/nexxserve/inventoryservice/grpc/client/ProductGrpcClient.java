//package com.nexxserve.inventoryservice.grpc.client;
//
//import com.nexxserve.catalog.grpc.GetProductRequest;
//import com.nexxserve.catalog.grpc.GetProductResponse;
//import com.nexxserve.catalog.grpc.GrpcProductType;
//import com.nexxserve.catalog.grpc.ProductServiceGrpc;
//import com.nexxserve.inventoryservice.exception.MedicineClientException;
//import com.nexxserve.inventoryservice.exception.ProductClientException;
//import io.grpc.ManagedChannel;
//import io.grpc.ManagedChannelBuilder;
//import io.grpc.Status;
//import io.grpc.StatusRuntimeException;
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PreDestroy;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class ProductGrpcClient {
//
//    private final DiscoveryClient discoveryClient;
//    private static final String SERVICE_NAME = "catalog";
//
//    @Value("${grpc.client.product.host:localhost}")
//    private String host;
//
//    @Value("${grpc.client.product.port:7090}")
//    private int port;
//
//    @Value("${grpc.client.product.timeout:30}")
//    private long timeoutSeconds;
//
//    private ManagedChannel channel;
//
//    @PostConstruct
//    public void init() {
//        log.info("Initializing gRPC client for Product service at {}:{}", host, port);
//
//        try {
//            List<ServiceInstance> instances = discoveryClient.getInstances(SERVICE_NAME);
//            if (instances == null || instances.isEmpty()) {
//                log.warn("No instances found for service: {}", SERVICE_NAME);
//            } else {
//                // Use the first available instance
//                ServiceInstance instance = instances.getFirst();
//                host = instance.getHost();
//                port = Integer.parseInt(instance.getMetadata().get("grpc-port"));
//                log.info("Using discovered service instance: {}:{}", host, port);
//            }
//        } catch (StatusRuntimeException e) {
//            throw new MedicineClientException("Failed to discover service instances for " + SERVICE_NAME, e);
//        }
//        channel = ManagedChannelBuilder.forAddress(host, port)
//                .usePlaintext()
//                .build();
//
//        // Create the blocking stub without a deadline
//        ProductServiceGrpc.ProductServiceBlockingStub blockingStub = ProductServiceGrpc.newBlockingStub(channel);
//
//        log.info("Product gRPC client initialized successfully");
//    }
//
//    @PreDestroy
//    public void cleanup() {
//        log.info("Shutting down Product gRPC client");
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
//     * Fetch product data by reference ID and product type
//     *
//     * @param referenceId The reference ID of the product
//     * @param productType The type of product (CATALOG_PRODUCT_FAMILY or CATALOG_PRODUCT_VARIANT)
//     * @return GetProductResponse containing the product details
//     * @throws ProductClientException if the request fails
//     */
//    public GetProductResponse getProductByReference(String referenceId, GrpcProductType productType) {
//        log.debug("Fetching product data for reference: {} and type: {}", referenceId, productType);
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
//                throw new ProductClientException("Invalid UUID format: " + referenceId, e);
//            }
//
//            GetProductRequest request = GetProductRequest.newBuilder()
//                    .setReferenceId(referenceId)
//                    .setProductType(productType)
//                    .build();
//
//            // Refresh the blocking stub with a new deadline before each call
//            ProductServiceGrpc.ProductServiceBlockingStub callStub =
//                ProductServiceGrpc.newBlockingStub(channel)
//                    .withDeadlineAfter(timeoutSeconds, TimeUnit.SECONDS);
//
//            GetProductResponse response = callStub.getProduct(request);
//
//            if (response == null || !response.getSuccess()) {
//                log.warn("No product data found for reference: {} and type: {}, error: {}",
//                        referenceId, productType, response != null ? response.getErrorMessage() : "null response");
//                throw new ProductClientException("Product not found with id: " + referenceId);
//            }
//
//            log.debug("Successfully retrieved product data for reference: {}", referenceId);
//            return response;
//
//        } catch (StatusRuntimeException e) {
//            log.error("gRPC call failed for reference: {} with status: {}", referenceId, e.getStatus(), e);
//
//            // More specific error handling based on status code
//            if (e.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED) {
//                throw new ProductClientException("Request timed out while fetching product data", e);
//            } else if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
//                throw new ProductClientException("Product not found with id: " + referenceId, e);
//            } else if (e.getStatus().getCode() == Status.Code.INVALID_ARGUMENT) {
//                throw new ProductClientException("Invalid product reference: " + referenceId, e);
//            } else {
//                throw new ProductClientException("Failed to fetch product data: " + e.getStatus().getDescription(), e);
//            }
//        } catch (Exception e) {
//            log.error("Unexpected error while fetching product data for reference: {}", referenceId, e);
//            throw new ProductClientException("Unexpected error occurred while fetching product data", e);
//        }
//    }
//
//    /**
//     * Convenience method for fetching product family
//     */
//    public GetProductResponse getProductFamily(String referenceId) {
//        return getProductByReference(referenceId, GrpcProductType.CATALOG_PRODUCT_FAMILY);
//    }
//
//    /**
//     * Convenience method for fetching product variant
//     */
//    public GetProductResponse getProductVariant(String referenceId) {
//        return getProductByReference(referenceId, GrpcProductType.CATALOG_PRODUCT_VARIANT);
//    }
//
//    /**
//     * Check if the gRPC channel is ready for communication
//     */
//    public boolean isChannelReady() {
//        return channel != null && !channel.isShutdown() && !channel.isTerminated();
//    }
//}