package com.nexxserve.inventoryservice.service.admin;

import com.nexxserve.inventoryservice.dto.ActivateClientRequest;
import com.nexxserve.inventoryservice.dto.ActivateClientResponse;
import com.nexxserve.inventoryservice.dto.Activationresponse;
import com.nexxserve.inventoryservice.dto.RefreshTokenResponse;
import com.nexxserve.inventoryservice.security.ClientCredentialsStore;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class ClientService {

    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);

    @Value("${client.name:Default Client}")
    private String clientName;

    @Value("${client.phone:+1234567890}")
    private String clientPhone;

    @Value("${client.baseUrl:http://localhost:5007}")
    private String clientBaseUrl;

    @Value("${server.url:http://localhost:8080}")
    private String serverUrl;

    private final RestTemplate restTemplate;
    private final ClientCredentialsStore credentialsStore;

    @Getter
    private String clientId;
    private String password;
    private String currentToken;
    private boolean isRegistered = false;
    private boolean isActivated = false;

    @Autowired
    public ClientService(ClientCredentialsStore credentialsStore) {
        this.restTemplate = new RestTemplate();
        this.credentialsStore = credentialsStore;
    }



    public Activationresponse activateClient(ActivateClientRequest request) {
        logger.info("Activating client via backend at {}", request.getServerUrl());
        Activationresponse response = new Activationresponse();
        try {
            String url = request.getServerUrl() + "/api/client/activate";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ActivateClientRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<ActivateClientResponse> backendResponse =
                    restTemplate.postForEntity(url, entity, ActivateClientResponse.class);

            if (backendResponse.getStatusCode() == HttpStatus.OK && backendResponse.getBody() != null) {
                ActivateClientResponse body = backendResponse.getBody();
                this.clientId = request.getClientId();
                this.password = request.getPassword();
                this.currentToken = body.getToken();
                this.isActivated = true;
                this.isRegistered = true;
                Instant now = Instant.now();

                // Save credentials and tokens
                credentialsStore.saveCredentials(clientId, password);
                credentialsStore.saveStatus(isRegistered, isActivated, currentToken, request.getServerUrl(), now);
                // Optionally, save refreshToken somewhere secure

                response.setActivated(true);
                response.setTokenSavedAt(now);
                response.setServerUrl(request.getServerUrl());
                response.setMessage("Activation successful");
                logger.info("Client activated and tokens saved.");
            } else {
                response.setMessage("Activation failed: " + backendResponse.getStatusCode());
                logger.warn("Activation failed with status: {}", backendResponse.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Activation error: {}", e.getMessage(), e);
            response.setMessage("Activation error: " + e.getMessage());
        }
        return response;
    }

   // In src/main/java/com/nexxserve/inventoryservice/service/admin/ClientService.java

   public void refreshTokenWithServer() {
       logger.info("Refreshing token with server using refresh token...");
       try {
           String url = serverUrl + "/api/client/refresh-token";

           // Load current status (including tokens)
           ClientCredentialsStore.ClientStatus status = credentialsStore.loadStatus();
           String refreshToken = status.getToken(); // Assuming refresh token is stored as 'token'
           if (refreshToken == null) {
               logger.warn("No refresh token found in store.");
               return;
           }

           HttpHeaders headers = new HttpHeaders();
           headers.setContentType(MediaType.APPLICATION_JSON);
           headers.set("RefreshToken", refreshToken);

           Map<String, String> body = new HashMap<>();
           body.put("clientId", clientId);

           HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

           ResponseEntity<RefreshTokenResponse> response = restTemplate.postForEntity(
                   url, entity, RefreshTokenResponse.class);

           if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
               RefreshTokenResponse resp = response.getBody();
               this.currentToken = resp.getAccessToken();

               // Save new tokens using the store's saveStatus method
               credentialsStore.saveStatus(
                   isRegistered,
                   isActivated,
                   resp.getRefreshToken(), // Save the new refresh token
                   serverUrl,
                   Instant.now()
               );
               logger.info("Tokens refreshed successfully. New access token and refresh token saved.");
           } else {
               logger.warn("Failed to refresh token. Status: {}", response.getStatusCode());
           }
       } catch (Exception e) {
           logger.error("Error refreshing token: {}", e.getMessage(), e);
       }
   }



    // Update the initialize method to call sync after activation
    @PostConstruct
    public void initialize() {
        logger.info("Initializing client service at startup...");

        // Load saved credentials and status
        ClientCredentialsStore.ClientCredentials credentials = credentialsStore.loadCredentials();
        ClientCredentialsStore.ClientStatus status = credentialsStore.loadStatus();

        if (credentials != null) {
            this.clientId = credentials.getClientId();
            this.password = credentials.getPassword();
            logger.info("Loaded stored client credentials for clientId: {}", clientId);
        }

        if (status != null) {
            this.isRegistered = status.isRegistered();
            this.isActivated = status.isActivated();
            this.currentToken = status.getToken();
            logger.info("Loaded client status - registered: {}, activated: {}, has token: {}",
                    isRegistered, isActivated, (currentToken != null));

            // If client is already activated, sync data with backend
            if (isActivated && currentToken != null) {

            }
        }

//        manageClientLifecycle(); // Run the lifecycle check immediately at startup
    }


//    @Scheduled(fixedDelay = 30000) // Check every 30 seconds
    public void manageClientLifecycle() {
        logger.info("Starting client lifecycle check...");
        try {
            if (!isRegistered) {
                logger.info("Client not registered. Attempting registration...");
                register();
            } else if (!isActivated) {
                logger.info("Client registered but not activated. Checking activation status...");
                checkActivation();
            } else {
                logger.info("Client active. Checking token status...");
                refreshTokenIfNeeded();
            }
        } catch (Exception e) {
            logger.error("Error in client lifecycle management: {}", e.getMessage(), e);
        }
        logger.info("Client lifecycle check completed");
    }

    private void register() {
        logger.info("Registering client with name: {}, phone: {}, baseUrl: {}", clientName, clientPhone, clientBaseUrl);
        try {
            String url = serverUrl + "/api/admin/clients/register";
            logger.info("Sending registration request to: {}", url);

            Map<String, String> request = new HashMap<>();
            request.put("name", clientName);
            request.put("phone", clientPhone);
            request.put("baseUrl", clientBaseUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            logger.debug("Registration request payload: {}", request);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            logger.info("Registration response status: {}", response.getStatusCode());

            if (response.getStatusCode() == HttpStatus.OK) {
                Map responseBody = response.getBody();
                assert responseBody != null;
                this.clientId = (String) responseBody.get("clientId");
                this.password = (String) responseBody.get("password");
                this.isRegistered = true;

                // Save credentials securely
                credentialsStore.saveCredentials(clientId, password);
//                credentialsStore.saveStatus(isRegistered, isActivated, currentToken);

                logger.info("Client registered successfully. ClientId: {}, Password: [REDACTED]", clientId);
                logger.info("Waiting for activation...");
            } else {
                logger.warn("Registration failed with status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Failed to register client: {}", e.getMessage(), e);
        }
    }

    private void checkActivation() {
        logger.info("Checking activation status for clientId: {}", clientId);
        try {
            String url = serverUrl + "/api/client/check-activation";
            logger.info("Sending activation check request to: {}", url);

            Map<String, String> request = new HashMap<>();
            request.put("clientId", clientId);
            request.put("password", password);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            logger.debug("Activation check request payload: {}", request);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            logger.info("Activation check response status: {}", response.getStatusCode());

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                Boolean activated = (Boolean) responseBody.get("activated");

                if (activated) {
                    this.currentToken = (String) responseBody.get("token");
                    this.isActivated = true;
                    logger.info("Client activated successfully! Token received and stored.");
                } else {
                    // CRITICAL FIX: Set to false when not activated
                    this.isActivated = false;
                    this.currentToken = null;
                    logger.info("Client not yet activated. Status: {}", responseBody.get("status"));
                }

                // CRITICAL FIX: Always save the updated status to database
//                credentialsStore.saveStatus(isRegistered, isActivated, currentToken);
                logger.info("Database updated with activation status: {}", isActivated);
            } else {
                logger.warn("Activation check failed with status: {}", response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            logger.error("HTTP error during activation check - Status: {}, Response: {}",
                    e.getStatusCode(), responseBody);

            // Handle specific error cases
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                if (responseBody.contains("Invalid client credentials")) {
                    logger.warn("Invalid client credentials detected. Resetting client and starting fresh registration...");
                    resetClientAndReregister();
                } else {
                    logger.error("Bad request error during activation check: {}", e.getMessage());
                }
            } else if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                logger.error("Internal server error during activation check. Server is experiencing issues. Will retry in next cycle.");
            } else {
                logger.error("HTTP error during activation check: {}", e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Unexpected error during activation check: {}", e.getMessage(), e);
        }
    }

    private void refreshTokenIfNeeded() {
        try {
            String url = serverUrl + "/api/client/refresh-token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(currentToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map responseBody = response.getBody();
                assert responseBody != null;
                String newToken = (String) responseBody.get("token");
                if (newToken != null) {
                    this.currentToken = newToken;

                    // Save updated token
//                    credentialsStore.saveStatus(isRegistered, isActivated, currentToken);

                    logger.info("Token refreshed successfully");
                }
            } else {
                logger.warn("Failed to refresh token. Getting new token...");
                getNewToken();
            }
        } catch (HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            logger.error("HTTP error during token refresh - Status: {}, Response: {}",
                    e.getStatusCode(), responseBody);

            if (e.getStatusCode() == HttpStatus.BAD_REQUEST ||
                e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                // Token is likely invalid - update client status
                logger.warn("Token appears to be invalid or expired");
                // Mark token as invalid but keep client registered
                this.currentToken = null;
//                credentialsStore.saveStatus(isRegistered, isActivated, null);

                // Try to get a new token with stored credentials
                getNewToken();
            } else if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                logger.error("Internal server error during token refresh. Will retry in next cycle.");
            } else {
                logger.error("HTTP error during token refresh: {}", e.getMessage());
                getNewToken();
            }
        } catch (Exception e) {
            logger.error("Unexpected error during token refresh: {}", e.getMessage());
            getNewToken();
        }
    }

    private void getNewToken() {
        try {
            logger.info("Attempting to get new token with clientId: {}", clientId);
            String url = serverUrl + "/api/client/get-token";

            Map<String, String> request = new HashMap<>();
            request.put("clientId", clientId);
            request.put("password", password);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map responseBody = response.getBody();
                assert responseBody != null;
                this.currentToken = (String) responseBody.get("token");

                // Save updated token
//                credentialsStore.saveStatus(isRegistered, isActivated, currentToken);
                logger.info("New token obtained successfully");
            }
        } catch (HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            logger.error("HTTP error during get new token - Status: {}, Response: {}",
                    e.getStatusCode(), responseBody);

            if (e.getStatusCode() == HttpStatus.BAD_REQUEST ||
                e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                // Check if it's an invalid credentials error
                if (responseBody.contains("Invalid client credentials") ||
                    responseBody.contains("Client may not be activated") ||
                    responseBody.contains("credentials are invalid")) {
                    logger.warn("Invalid client credentials detected. Resetting client...");
                    resetClientAndReregister();
                } else {
                    // Client might not be activated anymore
                    logger.warn("Client activation status may have changed. Marking as not activated.");
                    this.isActivated = false;
                    this.currentToken = null;
//                    credentialsStore.saveStatus(isRegistered, false, null);
                }
            } else if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                logger.error("Internal server error during get new token. Will retry in next cycle.");
            } else {
                logger.error("HTTP error during get new token: {}", e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Unexpected error during get new token: {}", e.getMessage(), e);
        }
    }

//    private void getNewToken() {
//        try {
//            String url = serverUrl + "/api/client/get-token";
//
//            Map<String, String> request = new HashMap<>();
//            request.put("clientId", clientId);
//            request.put("password", password);
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);
//
//            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
//
//            if (response.getStatusCode() == HttpStatus.OK) {
//                Map responseBody = response.getBody();
//                assert responseBody != null;
//                this.currentToken = (String) responseBody.get("token");
//
//                // Save updated token
//                credentialsStore.saveStatus(isRegistered, isActivated, currentToken);
//
//                logger.info("New token obtained successfully");
//            }
//        } catch (HttpClientErrorException e) {
//            String responseBody = e.getResponseBodyAsString();
//            logger.error("HTTP error during get new token - Status: {}, Response: {}",
//                    e.getStatusCode(), responseBody);
//
//            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
//                if (responseBody.contains("Invalid client credentials")) {
//                    logger.warn("Invalid client credentials during get new token. Resetting client...");
//                    resetClientAndReregister();
//                } else {
//                    logger.error("Bad request error during get new token: {}", e.getMessage());
//                }
//            } else if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
//                logger.error("Internal server error during get new token. Server is experiencing issues. Will retry in next cycle.");
//            } else {
//                logger.error("HTTP error during get new token: {}", e.getMessage());
//            }
//        } catch (Exception e) {
//            logger.error("Unexpected error during get new token: {}", e.getMessage());
//        }
//    }
    private void resetClientAndReregister() {
        logger.info("Resetting client credentials and status. Will re-register from scratch.");

        // Clear all in-memory state
        this.clientId = null;
        this.password = null;
        this.currentToken = null;
        this.isRegistered = false;
        this.isActivated = false;

        // Clear all stored credentials and status
        credentialsStore.clearAllCredentials();

        logger.info("Client reset completed. Will attempt fresh registration in next cycle.");
    }

    public void triggerAsyncStatusUpdate() {
        try {
            String url = serverUrl + "/api/client/check-activation";

            Map<String, String> request = new HashMap<>();
            request.put("clientId", clientId);
            request.put("password", password);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                Boolean activated = (Boolean) responseBody.get("activated");

                if (activated) {
                    this.currentToken = (String) responseBody.get("token");
                    this.isActivated = true;
                } else {
                    this.isActivated = false;
                }

                // Update database with latest status
//                credentialsStore.saveStatus(isRegistered, isActivated, currentToken);
                logger.info("Async status update completed. Client activation status: {}", isActivated);
            }
        } catch (Exception e) {
            logger.error("Async status update failed: {}", e.getMessage());
        }
    }

    // Getters for other services to use
    public String getCurrentToken() {
        return currentToken;
    }

   public boolean isClientActivated() {
       ClientCredentialsStore.ClientStatus status = credentialsStore.loadStatus();
       logger.info("Checking client activation status from database: registered={}, activated={}, hasToken={}",
               status.isRegistered(), status.isActivated(), (status.getToken() != null));

       if (status.isActivated() && status.getToken() == null) {
           logger.warn("Client marked as activated but no token found - forcing recheck");
           // Optionally update DB here
           return false;
       }

       return status.isActivated();
   }

}