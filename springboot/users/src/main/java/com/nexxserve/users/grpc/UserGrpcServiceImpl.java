package com.nexxserve.users.grpc;

import com.nexxserve.users.dto.UserDTO;
import com.nexxserve.users.exception.ResourceNotFoundException;
import com.nexxserve.users.exception.UserAlreadyExistsException;
import com.nexxserve.users.service.UserService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class UserGrpcServiceImpl extends UserGrpcServiceGrpc.UserGrpcServiceImplBase {

    private final UserService userService;

    @Autowired
    public UserGrpcServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void createUser(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            // Convert gRPC request to UserDTO
            UserDTO userDTO = new UserDTO();
            userDTO.setFirstName(request.getFirstName());
            userDTO.setEmail(request.getEmail());
            userDTO.setLastName(request.getLastName());
            userDTO.setUsername(request.getUsername());
            userDTO.setPhone(request.getPhone());
            userDTO.setProfileUrl(request.getProfileUrl());
            userDTO.setClinicId(request.getClinicId());

            // Call the existing service to create the user
            UserDTO createdUser = userService.createUser(userDTO);

            // Convert UserDTO to gRPC response
            UserResponse response = UserResponse.newBuilder()
                    .setUserId(createdUser.getUserId())
                    .setFirstName(createdUser.getFirstName())
                    .setEmail(createdUser.getEmail())
                    .setLastName(createdUser.getLastName() != null ? createdUser.getLastName() : "")
                    .setUsername(createdUser.getUsername() != null ? createdUser.getUsername() : "")
                    .setPhone(createdUser.getPhone() != null ? createdUser.getPhone() : "")
                    .setProfileUrl(createdUser.getProfileUrl() != null ? createdUser.getProfileUrl() : "")
                    .setClinicId(createdUser.getClinicId() != null ? createdUser.getClinicId() : "")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (UserAlreadyExistsException e) {
            responseObserver.onError(
                    Status.ALREADY_EXISTS
                            .withDescription(e.getMessage())
                            .withCause(e)
                            .asRuntimeException()
            );
        } catch (ResourceNotFoundException e) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription(e.getMessage())
                            .withCause(e)
                            .asRuntimeException()
            );
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("An internal error occurred")
                            .withCause(e)
                            .asRuntimeException()
            );
        }
    }
}