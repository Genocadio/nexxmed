package com.nexxserve.catalog.grpc;

import com.nexxserve.catalog.dto.InsuranceDto;
import com.nexxserve.catalog.enums.ApprovalType;
import com.nexxserve.catalog.enums.InsuranceStatus;
import com.nexxserve.catalog.service.InsuranceService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class InsuranceGrpcServiceImpl extends InsuranceGrpcServiceGrpc.InsuranceGrpcServiceImplBase {

    private final InsuranceService insuranceService;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;


    @Override
    public void getInsuranceById(InsuranceByIdRequest request, StreamObserver<InsuranceProto> responseObserver) {
        log.debug("gRPC request for getInsuranceById - fetching insurance with id: {}", request.getId());

        try {
            // Convert string ID to UUID
            UUID insuranceId = UUID.fromString(request.getId());

            // Get insurance by ID
            InsuranceDto insuranceDto = insuranceService.findById(insuranceId);

            // Map DTO to Proto message
            InsuranceProto.Builder protoBuilder = InsuranceProto.newBuilder();

            // Handle required and optional fields
            if (insuranceDto.getId() != null) protoBuilder.setId(insuranceDto.getId().toString());
            if (insuranceDto.getName() != null) protoBuilder.setName(insuranceDto.getName());
            if (insuranceDto.getCode() != null) protoBuilder.setCode(insuranceDto.getCode());
            if (insuranceDto.getDescription() != null) protoBuilder.setDescription(insuranceDto.getDescription());

            // Map status enum
            if (insuranceDto.getStatus() != null) {
                protoBuilder.setStatus(mapInsuranceStatusToProto(insuranceDto.getStatus()));
            }

            // Contact information
            if (insuranceDto.getContactEmail() != null) protoBuilder.setContactEmail(insuranceDto.getContactEmail());
            if (insuranceDto.getContactPhone() != null) protoBuilder.setContactPhone(insuranceDto.getContactPhone());
            if (insuranceDto.getAddress() != null) protoBuilder.setAddress(insuranceDto.getAddress());

            // Approval settings
            if (insuranceDto.getRequiresPreApproval() != null) protoBuilder.setRequiresPreApproval(insuranceDto.getRequiresPreApproval());
            if (insuranceDto.getDefaultApprovalType() != null) {
                protoBuilder.setDefaultApprovalType(mapApprovalTypeToProto(insuranceDto.getDefaultApprovalType()));
            }

            // Financial values
            if (insuranceDto.getDefaultClientContributionPercentage() != null) {
                protoBuilder.setDefaultClientContributionPercentage(
                        insuranceDto.getDefaultClientContributionPercentage().doubleValue());
            }
            if (insuranceDto.getMaxCoverageAmount() != null) {
                protoBuilder.setMaxCoverageAmount(insuranceDto.getMaxCoverageAmount().doubleValue());
            }

            // Audit fields
            if (insuranceDto.getCreatedAt() != null) {
                protoBuilder.setCreatedAt(insuranceDto.getCreatedAt().format(DATE_TIME_FORMATTER));
            }
            if (insuranceDto.getUpdatedAt() != null) {
                protoBuilder.setUpdatedAt(insuranceDto.getUpdatedAt().format(DATE_TIME_FORMATTER));
            }
            if (insuranceDto.getCreatedBy() != null) protoBuilder.setCreatedBy(insuranceDto.getCreatedBy());
            if (insuranceDto.getUpdatedBy() != null) protoBuilder.setUpdatedBy(insuranceDto.getUpdatedBy());
            if (insuranceDto.getVersion() != null) protoBuilder.setVersion(insuranceDto.getVersion());

            // Send response
            responseObserver.onNext(protoBuilder.build());
            responseObserver.onCompleted();
            log.debug("gRPC getInsuranceById response sent for insurance ID: {}", request.getId());
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format in getInsuranceById request", e);
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT
                    .withDescription("Invalid insurance ID format: " + e.getMessage())
                    .asRuntimeException());
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                log.error("Insurance not found in getInsuranceById request", e);
                responseObserver.onError(io.grpc.Status.NOT_FOUND
                        .withDescription("Insurance not found with ID: " + request.getId())
                        .asRuntimeException());
            } else {
                log.error("Error while processing getInsuranceById request", e);
                responseObserver.onError(io.grpc.Status.INTERNAL
                        .withDescription("Error processing request: " + e.getMessage())
                        .asRuntimeException());
            }
        }
    }

    // Helper methods to map enums
    private InsuranceStatusProto mapInsuranceStatusToProto(InsuranceStatus status) {
        return switch (status) {
            case ACTIVE -> InsuranceStatusProto.ACTIVE;
            case INACTIVE -> InsuranceStatusProto.INACTIVE;
            case SUSPENDED -> InsuranceStatusProto.SUSPENDED;
            case PENDING_APPROVAL -> InsuranceStatusProto.PENDING_APPROVAL;
            default -> InsuranceStatusProto.UNKNOWN;
        };
    }

    private ApprovalTypeProto mapApprovalTypeToProto(ApprovalType approvalType) {
        return switch (approvalType) {
            case AUTOMATIC -> ApprovalTypeProto.AUTOMATIC;
            case MANUAL_APPROVAL -> ApprovalTypeProto.MANUAL_APPROVAL;
            case CONDITIONAL -> ApprovalTypeProto.CONDITIONAL;
            case PRE_AUTHORIZATION -> ApprovalTypeProto.PRE_AUTHORIZATION;
            default -> ApprovalTypeProto.UNKNOWN_APPROVAL;
        };
    }
}