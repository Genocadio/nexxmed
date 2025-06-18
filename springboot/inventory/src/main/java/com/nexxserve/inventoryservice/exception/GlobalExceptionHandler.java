package com.nexxserve.inventoryservice.exception;

    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.http.converter.HttpMessageNotReadableException;
    import org.springframework.web.bind.MethodArgumentNotValidException;
    import org.springframework.web.bind.annotation.ControllerAdvice;
    import org.springframework.web.bind.annotation.ExceptionHandler;
    import org.springframework.web.bind.ServletRequestBindingException;
    import org.springframework.web.bind.MissingServletRequestParameterException;
    import org.springframework.web.context.request.WebRequest;
    import org.springframework.web.servlet.NoHandlerFoundException;

    import java.time.LocalDateTime;
    import java.util.Arrays;
    import java.util.HashMap;
    import java.util.Map;

    @ControllerAdvice
    public class GlobalExceptionHandler {
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<CustomErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
            CustomErrorResponse errorResponse = new CustomErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    ex.getMessage(),
                    request.getDescription(false)
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(ProductClientException.class)
        public ResponseEntity<CustomErrorResponse> handleProductClientException(ProductClientException ex, WebRequest request) {
            String message = ex.getMessage();
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // Default status

            // Get the root cause
            Throwable rootCause = ex;
            while (rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
            }

            if (message != null) {
                // UUID formatting issues
                if (message.contains("Invalid UUID format") ||
                    (rootCause instanceof NumberFormatException && rootCause.getMessage().contains("Error at index"))) {
                    status = HttpStatus.BAD_REQUEST;
                    message = "Invalid product reference ID format";
                }
                // Not found issues - expanded to match more patterns
                else if (message.contains("Product not found") ||
                        message.contains("not found with id") ||
                        message.contains("Product variant not found") ||
                        message.contains("No product data found")) {
                    status = HttpStatus.NOT_FOUND;
                    message = "Product not found with the specified identifier";
                }
                // Invalid argument issues
                else if (message.contains("Invalid product reference") || message.contains("Invalid argument")) {
                    status = HttpStatus.BAD_REQUEST;
                }
                // Timeout issues
                else if (message.contains("Request timed out") || message.contains("DEADLINE_EXCEEDED")) {
                    status = HttpStatus.GATEWAY_TIMEOUT;
                    message = "Product service request timed out";
                }
                // Unsupported operation issues
                else if (message.contains("Unsupported operation") || message.contains("UNIMPLEMENTED")) {
                    status = HttpStatus.BAD_REQUEST;
                    message = "Unsupported operation in product service";
                }
                // Unexpected errors should be masked with a generic message
                else {
                    status = HttpStatus.INTERNAL_SERVER_ERROR;
                    message = "Unable to process request due to a system error";
                }
            }

            CustomErrorResponse errorResponse = new CustomErrorResponse(
                    LocalDateTime.now(),
                    status.value(),
                    status.getReasonPhrase(),
                    message,
                    request.getDescription(false)
            );

            return new ResponseEntity<>(errorResponse, status);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<CustomErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
            Map<String, String> errors = new HashMap<>();
            ex.getBindingResult().getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
            });

            String errorMessage = "Validation failed";
            if (ex.getBindingResult().getFieldError() != null) {
                errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
            }

            CustomErrorResponse errorResponse = new CustomErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    errorMessage,
                    request.getDescription(false)
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }


        @ExceptionHandler(ServletRequestBindingException.class)
        public ResponseEntity<CustomErrorResponse> handleServletRequestBindingException(ServletRequestBindingException ex, WebRequest request) {
            CustomErrorResponse errorResponse = new CustomErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    "Required request parameters or body are missing",
                    request.getDescription(false)
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<CustomErrorResponse> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, WebRequest request) {
            CustomErrorResponse errorResponse = new CustomErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    "Required request parameter '" + ex.getParameterName() + "' is missing",
                    request.getDescription(false)
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<CustomErrorResponse> handleMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
            String errorMessage = "Required request body is missing or malformed";

            // Check if it's an enum conversion issue
            if (ex.getCause() instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException) {
                com.fasterxml.jackson.databind.exc.InvalidFormatException ife =
                    (com.fasterxml.jackson.databind.exc.InvalidFormatException) ex.getCause();
                if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
                    errorMessage = String.format(
                        "Invalid value '%s' for field '%s'. Accepted values are: %s",
                        ife.getValue(),
                        ife.getPath().isEmpty() ? "unknown" : ife.getPath().get(0).getFieldName(),
                        Arrays.toString(ife.getTargetType().getEnumConstants())
                    );
                }
            }

            CustomErrorResponse errorResponse = new CustomErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    errorMessage,
                    request.getDescription(false)
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(NoHandlerFoundException.class)
        public ResponseEntity<CustomErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex, WebRequest request) {
            CustomErrorResponse errorResponse = new CustomErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL(),
                    request.getDescription(false)
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

       @ExceptionHandler(MedicineClientException.class)
        public ResponseEntity<CustomErrorResponse> handleMedicineClientException(MedicineClientException ex, WebRequest request) {
            String message = ex.getMessage();
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // Default status

            // Get the root cause
            Throwable rootCause = ex;
            while (rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
            }

//            log.debug("Processing MedicineClientException: {}, root cause: {}", message, rootCause.getMessage());

            if (message != null) {
                // UUID formatting issues
                if (message.contains("Invalid UUID format") ||
                    (rootCause instanceof NumberFormatException && rootCause.getMessage().contains("Error at index"))) {
                    status = HttpStatus.BAD_REQUEST;
                    message = "Invalid medicine reference ID format";
                }
                // Not found issues
                else if (message.contains("Medicine not found") || message.contains("not found with id")) {
                    status = HttpStatus.NOT_FOUND;
                }
                // Invalid argument issues
                else if (message.contains("Invalid medicine reference") || message.contains("Invalid argument")) {
                    status = HttpStatus.BAD_REQUEST;
                }
                // Timeout issues
                else if (message.contains("Request timed out") || message.contains("DEADLINE_EXCEEDED")) {
                    status = HttpStatus.GATEWAY_TIMEOUT;
                    message = "Medicine service request timed out";
                }
                // Unsupported operation issues
                else if (message.contains("Unsupported operation") || message.contains("UNIMPLEMENTED")) {
                    status = HttpStatus.BAD_REQUEST;
                    message = "Unsupported operation in medicine service";
                }
                // Unexpected errors should be masked with a generic message
                else if (message.contains("Unexpected error occurred")) {
                    status = HttpStatus.INTERNAL_SERVER_ERROR;
                    message = "Unable to process request due to a system error";
                }
            }

            CustomErrorResponse errorResponse = new CustomErrorResponse(
                    LocalDateTime.now(),
                    status.value(),
                    status.getReasonPhrase(),
                    message,
                    request.getDescription(false)
            );

            return new ResponseEntity<>(errorResponse, status);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<CustomErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
            // Check if it's a missing body exception that wasn't caught by the specific handlers
            if (ex.getMessage() != null && ex.getMessage().contains("Required request body is missing")) {
                CustomErrorResponse errorResponse = new CustomErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        "Bad Request",
                        "Required request body is missing",
                        request.getDescription(false)
                );
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            CustomErrorResponse errorResponse = new CustomErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    ex.getMessage(),
                    request.getDescription(false)
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }