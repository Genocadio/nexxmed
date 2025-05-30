package com.nexxserve.users.grpc;

            import com.nexxserve.users.dto.UserDTO;
            import com.nexxserve.users.exception.ResourceNotFoundException;
            import com.nexxserve.users.service.UserService;
            import io.grpc.Status;
            import io.grpc.stub.StreamObserver;
            import net.devh.boot.grpc.server.service.GrpcService;
            import org.springframework.beans.factory.annotation.Autowired;

            @GrpcService
            public class UserSearchServiceImpl extends UserServiceGrpc.UserServiceImplBase {

                private final UserService userService;

                @Autowired
                public UserSearchServiceImpl(UserService userService) {
                    this.userService = userService;
                }

                @Override
                public void searchUser(SearchUserRequest request, StreamObserver<SearchUserResponse> responseObserver) {
                    try {
                        UserDTO userDTO = userService.findUserBySearchParameter(request.getSearchParameter());

                        // Using the SearchUserResponse from searchuser.proto
                        SearchUserResponse response = SearchUserResponse.newBuilder()
                                .setUserId(userDTO.getUserId())
                                .setFirstName(userDTO.getFirstName())
                                .setLastName(userDTO.getLastName() != null ? userDTO.getLastName() : "")
                                .setEmail(userDTO.getEmail())
                                .setPhone(userDTO.getPhone() != null ? userDTO.getPhone() : "")
                                .setProfileUrl(userDTO.getProfileUrl() != null ? userDTO.getProfileUrl() : "")
                                .setClinicId(userDTO.getClinicId() != null ? userDTO.getClinicId() : "")
                                .setUsername(userDTO.getUsername() != null ? userDTO.getUsername() : "")
                                .build();

                        responseObserver.onNext(response);
                        responseObserver.onCompleted();
                    } catch (ResourceNotFoundException e) {
                        responseObserver.onError(
                                Status.NOT_FOUND
                                        .withDescription("User not found: " + e.getMessage())
                                        .asRuntimeException());
                    } catch (IllegalArgumentException e) {
                        responseObserver.onError(
                                Status.INVALID_ARGUMENT
                                        .withDescription(e.getMessage())
                                        .asRuntimeException());
                    } catch (Exception e) {
                        responseObserver.onError(
                                Status.INTERNAL
                                        .withDescription("Internal error: " + e.getMessage())
                                        .asRuntimeException());
                    }
                }
            }