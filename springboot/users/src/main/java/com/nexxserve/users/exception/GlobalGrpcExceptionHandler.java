package com.nexxserve.users.exception;

import com.nexxserve.users.exception.ResourceNotFoundException;
import com.nexxserve.users.exception.UserAlreadyExistsException;
import io.grpc.Status;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class GlobalGrpcExceptionHandler {

    @GrpcExceptionHandler(ResourceNotFoundException.class)
    public Status handleResourceNotFoundException(ResourceNotFoundException e) {
        return Status.NOT_FOUND.withDescription(e.getMessage()).withCause(e);
    }

    @GrpcExceptionHandler(UserAlreadyExistsException.class)
    public Status handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        return Status.ALREADY_EXISTS.withDescription(e.getMessage()).withCause(e);
    }

    @GrpcExceptionHandler(Exception.class)
    public Status handleGenericException(Exception e) {
        return Status.INTERNAL.withDescription("An internal server error occurred").withCause(e);
    }
}