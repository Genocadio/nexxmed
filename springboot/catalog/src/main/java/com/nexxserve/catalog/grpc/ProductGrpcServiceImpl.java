package com.nexxserve.catalog.grpc;

import com.nexxserve.catalog.dto.ProductFamilyDto;
import com.nexxserve.catalog.dto.ProductVariantDto;
import com.nexxserve.catalog.exception.ResourceNotFoundException;
import com.nexxserve.catalog.grpc.*;
import com.nexxserve.catalog.mapper.ProductGrpcMapper;
import com.nexxserve.catalog.service.ProductFamilyService;
import com.nexxserve.catalog.service.ProductVariantService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class ProductGrpcServiceImpl extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductFamilyService productFamilyService;
    private final ProductVariantService productVariantService;
    private final ProductGrpcMapper productGrpcMapper;

   @Override
    public void getProduct(GetProductRequest request, StreamObserver<GetProductResponse> responseObserver) {
        try {
            log.debug("Received gRPC request for product with referenceId: {} and type: {}",
                    request.getReferenceId(), request.getProductType());

            // Validate request
            request.getReferenceId();
            if (request.getReferenceId().trim().isEmpty()) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Reference ID cannot be null or empty")
                        .asRuntimeException());
                return;
            }

            if (request.getProductTypeValue() == 0 && request.getProductType().toString().isEmpty()) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Product type must be explicitly specified")
                        .asRuntimeException());
                return;
            }

            UUID referenceId;
            try {
                referenceId = UUID.fromString(request.getReferenceId());
            } catch (IllegalArgumentException e) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Invalid UUID format for reference ID")
                        .asRuntimeException());
                return;
            }

            GetProductResponse.Builder responseBuilder = GetProductResponse.newBuilder();

            switch (request.getProductType()) {
                case CATALOG_PRODUCT_FAMILY:
                    handleProductFamily(referenceId, responseBuilder, responseObserver);
                    break;
                case CATALOG_PRODUCT_VARIANT:
                    handleProductVariant(referenceId, responseBuilder, responseObserver);
                    break;
                default:
                    responseObserver.onError(Status.INVALID_ARGUMENT
                            .withDescription("Unsupported product type: " + request.getProductType())
                            .asRuntimeException());
                    return;
            }

        } catch (Exception e) {
            log.error("Unexpected error processing gRPC request for referenceId: {}", request.getReferenceId(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    private void handleProductFamily(UUID referenceId, GetProductResponse.Builder responseBuilder,
                                   StreamObserver<GetProductResponse> responseObserver) {
        try {
            ProductFamilyDto familyDto;
            try {
                familyDto = productFamilyService.findById(referenceId);
            } catch (ResourceNotFoundException e) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Product family not found with ID: " + referenceId)
                        .asRuntimeException());
                return;
            } catch (RuntimeException e) {
                // Re-throw if it's not a "not found" exception
                throw e;
            }

            GrpcProductFamilyDetails familyDetails = productGrpcMapper.toGrpcProductFamily(familyDto);
            responseBuilder.setFamily(familyDetails);
            responseBuilder.setSuccess(true);

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
            log.debug("Successfully mapped product family with ID: {}", referenceId);
        } catch (Exception e) {
            log.error("Error processing product family with ID: {}", referenceId, e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error processing product family: " + e.getMessage())
                    .asRuntimeException());
        }
    }

   private void handleProductVariant(UUID referenceId, GetProductResponse.Builder responseBuilder,
                                    StreamObserver<GetProductResponse> responseObserver) {
        try {
            ProductVariantDto variantDto;
            try {
                variantDto = productVariantService.findById(referenceId);
            } catch (ResourceNotFoundException e) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Product variant not found with ID: " + referenceId)
                        .asRuntimeException());
                return;
            } catch (RuntimeException e) {
                // Re-throw if it's not a "not found" exception
                throw e;
            }

            ProductVariantDetails variantDetails = productGrpcMapper.toGrpcProductVariant(variantDto);
            responseBuilder.setVariant(variantDetails);
            responseBuilder.setSuccess(true);

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
            log.debug("Successfully mapped product variant with ID: {}", referenceId);
        } catch (Exception e) {
            log.error("Error processing product variant with ID: {}", referenceId, e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error processing product variant: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}