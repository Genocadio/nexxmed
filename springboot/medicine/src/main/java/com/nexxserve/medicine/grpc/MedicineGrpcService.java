
package com.nexxserve.medicine.grpc;

import com.nexxserve.medicine.dto.BrandDto;
import com.nexxserve.medicine.dto.GenericDto;
import com.nexxserve.medicine.dto.VariantDto;
import com.nexxserve.medicine.exception.EntityNotFoundException;
import com.nexxserve.medicine.grpc.MedicineProto.*;
import com.nexxserve.medicine.grpc.MedicineServiceGrpc;
import com.nexxserve.medicine.service.BrandService;
import com.nexxserve.medicine.service.GenericService;
import com.nexxserve.medicine.service.VariantService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicineGrpcService extends MedicineServiceGrpc.MedicineServiceImplBase {

    private final GenericService genericService;
    private final BrandService brandService;
    private final VariantService variantService;

    @Override
    public void getMedicineByReference(MedicineReferenceRequest request,
                                       StreamObserver<MedicineResponse> responseObserver) {
        try {
            // Validate request
            if (request.getReferenceId().isEmpty()) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Reference ID cannot be empty")
                        .asRuntimeException());
                return;
            }

            if (request.getProductType() == ProductType.UNRECOGNIZED) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Invalid product type provided")
                        .asRuntimeException());
                return;
            }

            log.info("Received gRPC request for reference_id: {} with product_type: {}",
                    request.getReferenceId(), request.getProductType());

            MedicineResponse response = buildMedicineResponse(request);

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            log.error("Invalid argument in request: {}", e.getMessage(), e);
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid request parameter: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        } catch (EntityNotFoundException e) {
            log.error("Entity not found: {}", e.getMessage(), e);
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Resource not found: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        } catch (UnsupportedOperationException e) {
            log.error("Unsupported operation: {}", e.getMessage(), e);
            responseObserver.onError(Status.UNIMPLEMENTED
                    .withDescription("Unsupported operation: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("Error processing gRPC request: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    private MedicineResponse buildMedicineResponse(MedicineReferenceRequest request) {
        UUID referenceId;
        try {
            referenceId = UUID.fromString(request.getReferenceId());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format: " + request.getReferenceId());
        }

        MedicineResponse.Builder responseBuilder = MedicineResponse.newBuilder()
                .setProductType(request.getProductType());

        switch (request.getProductType()) {
            case MEDICINE_GENERIC:
                try {
                    GenericDto generic = genericService.findById(referenceId);
                    if (generic == null) {
                        throw new EntityNotFoundException("Generic medicine not found with id: " + referenceId);
                    }
                    return buildGenericResponse(responseBuilder, generic);
                } catch (Exception e) {
                    if (e instanceof EntityNotFoundException) {
                        throw e;
                    }
                    throw new RuntimeException("Error retrieving generic medicine: " + e.getMessage(), e);
                }

            case MEDICINE_BRAND:
                try {
                    BrandDto brand = brandService.findById(referenceId);
                    if (brand == null) {
                        throw new EntityNotFoundException("Brand medicine not found with id: " + referenceId);
                    }
                    return buildBrandResponse(responseBuilder, brand);
                } catch (Exception e) {
                    if (e instanceof EntityNotFoundException) {
                        throw e;
                    }
                    throw new RuntimeException("Error retrieving brand medicine: " + e.getMessage(), e);
                }

            case MEDICINE_VARIANT:
                try {
                    VariantDto variant = variantService.findById(referenceId);
                    if (variant == null) {
                        throw new EntityNotFoundException("Variant medicine not found with id: " + referenceId);
                    }
                    return buildVariantResponse(responseBuilder, variant);
                } catch (Exception e) {
                    if (e instanceof EntityNotFoundException) {
                        throw e;
                    }
                    throw new RuntimeException("Error retrieving variant medicine: " + e.getMessage(), e);
                }

            default:
                throw new UnsupportedOperationException("Unsupported product type: " + request.getProductType());
        }
    }

    private MedicineResponse buildGenericResponse(MedicineResponse.Builder builder, GenericDto generic) {
        GenericDetails.Builder genericDetails = GenericDetails.newBuilder()
                .setChemicalName(generic.getChemicalName() != null ? generic.getChemicalName() : "")
                .setDescription(generic.getDescription() != null ? generic.getDescription() : "")
                .setIsParent(generic.getIsParent() != null ? generic.getIsParent() : false);

        if (generic.getClassId() != null) {
            genericDetails.setClassId(generic.getClassId().toString());
        }
        if (generic.getClassName() != null) {
            genericDetails.setClassName(generic.getClassName());
        }

        builder.setId(generic.getId().toString())
                .setName(generic.getName())
                .setGenericDetails(genericDetails.build());

        addCommonFields(builder, generic.getCreatedAt(), generic.getUpdatedAt());
        addInsuranceCoverages(builder, generic.getInsuranceCoverages());

        return builder.build();
    }

    private MedicineResponse buildBrandResponse(MedicineResponse.Builder builder, BrandDto brand) {
        BrandDetails.Builder brandDetails = BrandDetails.newBuilder()
                .setBrandName(brand.getBrandName())
                .setManufacturer(brand.getManufacturer() != null ? brand.getManufacturer() : "")
                .setCountry(brand.getCountry() != null ? brand.getCountry() : "")
                .setVariantId(brand.getVariantId().toString())
                .setVariantName(brand.getVariantName() != null ? brand.getVariantName() : "");

        builder.setId(brand.getId().toString())
                .setName(brand.getBrandName())
                .setBrandDetails(brandDetails.build());

        addCommonFields(builder, brand.getCreatedAt(), brand.getUpdatedAt());
        addInsuranceCoverages(builder, brand.getInsuranceCoverages());

        return builder.build();
    }

    private MedicineResponse buildVariantResponse(MedicineResponse.Builder builder, VariantDto variant) {
        VariantDetails.Builder variantDetails = VariantDetails.newBuilder()
                .setForm(variant.getForm() != null ? variant.getForm() : "")
                .setRoute(variant.getRoute() != null ? variant.getRoute() : "")
                .setTradeName(variant.getTradeName() != null ? variant.getTradeName() : "")
                .setStrength(variant.getStrength() != null ? variant.getStrength() : "")
                .setConcentration(variant.getConcentration() != null ? variant.getConcentration() : "")
                .setPackaging(variant.getPackaging() != null ? variant.getPackaging() : "")
                .setNotes(variant.getNotes() != null ? variant.getNotes() : "");

        // Add generic IDs
        if (variant.getGenericIds() != null) {
            variant.getGenericIds().forEach(id ->
                    variantDetails.addGenericIds(id.toString()));
        }

        // Add generic info
        if (variant.getGenerics() != null) {
            variant.getGenerics().forEach(generic -> {
                GenericInfo genericInfo = GenericInfo.newBuilder()
                        .setId(generic.getId().toString())
                        .setName(generic.getName())
                        .setChemicalName(generic.getChemicalName() != null ? generic.getChemicalName() : "")
                        .build();
                variantDetails.addGenerics(genericInfo);
            });
        }

        // Add extra info
        if (variant.getExtraInfo() != null) {
            variant.getExtraInfo().forEach((key, value) ->
                    variantDetails.putExtraInfo(key, value != null ? value.toString() : ""));
        }

        builder.setId(variant.getId().toString())
                .setName(variant.getName())
                .setVariantDetails(variantDetails.build());

        addCommonFields(builder, variant.getCreatedAt(), variant.getUpdatedAt());
        addInsuranceCoverages(builder, variant.getInsuranceCoverages());

        return builder.build();
    }

    private void addCommonFields(MedicineResponse.Builder builder,
                                 java.time.LocalDateTime createdAt,
                                 java.time.LocalDateTime updatedAt) {
        if (createdAt != null) {
            builder.setCreatedAt(createdAt.toString());
        }
        if (updatedAt != null) {
            builder.setUpdatedAt(updatedAt.toString());
        }
    }

    private void addInsuranceCoverages(MedicineResponse.Builder builder,
                                      java.util.List<com.nexxserve.medicine.dto.InsuranceCoverageResponseDto> coverages) {
        if (coverages != null) {
            coverages.forEach(coverage -> {
                InsuranceCoverage.Builder coverageBuilder = InsuranceCoverage.newBuilder()
                        .setId(coverage.getId() != null ? coverage.getId().toString() : "")
                        .setInsuranceName(coverage.getInsuranceName() != null ? coverage.getInsuranceName() : "");

                // The coverage_type field in proto should map to status in the DTO
                if (coverage.getStatus() != null) {
                    coverageBuilder.setCoverageType(coverage.getStatus().toString());
                }

                // Map notes to approvalNotes
                if (coverage.getApprovalNotes() != null) {
                    coverageBuilder.setNotes(coverage.getApprovalNotes());
                }

                // Map coverage percentage from insuranceCoveragePercentage
                if (coverage.getInsuranceCoveragePercentage() != null) {
                    coverageBuilder.setCoveragePercentage(coverage.getInsuranceCoveragePercentage().doubleValue());
                }

                // Map max amount from maxCoverageAmount
                if (coverage.getMaxCoverageAmount() != null) {
                    coverageBuilder.setMaxAmount(coverage.getMaxCoverageAmount().doubleValue());
                }

                builder.addInsuranceCoverages(coverageBuilder.build());
            });
        }
    }
}