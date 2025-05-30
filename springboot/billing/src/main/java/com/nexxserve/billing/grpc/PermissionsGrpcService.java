package com.nexxserve.billing.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

@GrpcService
public class PermissionsGrpcService extends PermissionsServiceGrpc.PermissionsServiceImplBase {

    @Value("${spring.application.name:Billing Service}")
    private String serviceName;

    @Override
    public void getPermissions(PermissionsRequest request, StreamObserver<PermissionsResponse> responseObserver) {
        // Create the same hardcoded permissions that we have in the REST controller
        List<PermissionProto> permissionProtos = new ArrayList<>();

        // Admin permissions
        addPermission(permissionProtos, "billing.service.create", "Create billing services", "ADMIN");
        addPermission(permissionProtos, "billing.service.read", "Read billing services", "ADMIN");
        addPermission(permissionProtos, "billing.service.update", "Update billing services", "ADMIN");
        addPermission(permissionProtos, "billing.service.delete", "Delete billing services", "ADMIN");

        addPermission(permissionProtos, "billing.activity.create", "Create service activities", "ADMIN");
        addPermission(permissionProtos, "billing.activity.read", "Read service activities", "ADMIN");
        addPermission(permissionProtos, "billing.activity.update", "Update service activities", "ADMIN");
        addPermission(permissionProtos, "billing.activity.delete", "Delete service activities", "ADMIN");

        addPermission(permissionProtos, "billing.consumable.create", "Create consumables", "ADMIN");
        addPermission(permissionProtos, "billing.consumable.read", "Read consumables", "ADMIN");
        addPermission(permissionProtos, "billing.consumable.update", "Update consumables", "ADMIN");
        addPermission(permissionProtos, "billing.consumable.delete", "Delete consumables", "ADMIN");

        // Manager permissions
        addPermission(permissionProtos, "billing.service.read", "Read billing services", "MANAGER");
        addPermission(permissionProtos, "billing.activity.read", "Read service activities", "MANAGER");
        addPermission(permissionProtos, "billing.activity.create", "Create service activities", "MANAGER");
        addPermission(permissionProtos, "billing.activity.update", "Update service activities", "MANAGER");
        addPermission(permissionProtos, "billing.consumable.read", "Read consumables", "MANAGER");

        // User permissions
        addPermission(permissionProtos, "billing.service.read", "Read billing services", "USER");
        addPermission(permissionProtos, "billing.activity.read", "Read service activities", "USER");
        addPermission(permissionProtos, "billing.consumable.read", "Read consumables", "USER");

        // Build the response
        PermissionsResponse response = PermissionsResponse.newBuilder()
                .addAllPermissions(permissionProtos)
                .setServiceName(serviceName)
                .build();

        // Send the response and complete
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private void addPermission(List<PermissionProto> permissions, String permission, String description, String role) {
        permissions.add(PermissionProto.newBuilder()
                .setPermission(permission)
                .setDescription(description)
                .setRole(role)
                .build());
    }
}