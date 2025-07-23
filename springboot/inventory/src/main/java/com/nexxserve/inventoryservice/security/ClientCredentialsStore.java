package com.nexxserve.inventoryservice.security;

import com.nexxserve.inventoryservice.entity.ClientCredentialEntity;
import com.nexxserve.inventoryservice.repository.ClientCredentialRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Component
@Slf4j
public class ClientCredentialsStore {
    private static final String CLIENT_ID = "client_singleton";

    @Value("${app.credentials.secret:#{T(java.util.UUID).randomUUID().toString()}}")
    private String encryptionSecret;

    @Value("${app.credentials.salt:5c0744940b5c369b}")
    private String salt;

    private final ClientCredentialRepository repository;
    private TextEncryptor encryptor;

    @Autowired
    public ClientCredentialsStore(ClientCredentialRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        encryptor = Encryptors.text(encryptionSecret, salt);
    }

    public void saveCredentials(String clientId, String password) {
        ClientCredentialEntity entity = getOrCreateEntity();

        entity.setEncryptedClientId(encryptor.encrypt(clientId));
        entity.setEncryptedPassword(encryptor.encrypt(password));
        entity.setRegistered(true);

        repository.save(entity);
    }

    public void saveStatus(boolean isRegistered, boolean isActivated, String token) {
        log.info("Saving client status to database: registered={}, activated={}, hasToken={}",
                isRegistered, isActivated, (token != null));

        ClientCredentialEntity entity = getOrCreateEntity();

        entity.setRegistered(isRegistered);
        entity.setActivated(isActivated);

        if (token != null) {
            entity.setEncryptedToken(encryptor.encrypt(token));
        } else {
            entity.setEncryptedToken(null);
        }

        repository.save(entity);
        log.info("Client status saved successfully");
    }

    public ClientCredentials loadCredentials() {
        Optional<ClientCredentialEntity> entityOpt = repository.findById(CLIENT_ID);

        if (entityOpt.isPresent()) {
            ClientCredentialEntity entity = entityOpt.get();

            if (entity.getEncryptedClientId() != null && entity.getEncryptedPassword() != null) {
                try {
                    return new ClientCredentials(
                        encryptor.decrypt(entity.getEncryptedClientId()),
                        encryptor.decrypt(entity.getEncryptedPassword())
                    );
                } catch (Exception e) {
                    log.error("Failed to decrypt client credentials: {}", e.getMessage());
                    // Reset credentials if they can't be decrypted
                    entity.setEncryptedClientId(null);
                    entity.setEncryptedPassword(null);
                    entity.setRegistered(false);
                    entity.setActivated(false);
                    entity.setEncryptedToken(null);
                    repository.save(entity);
                }
            }
        }

        return null;
    }

    public void clearAllCredentials() {
        log.info("Clearing all client credentials and status from database");

        try {
            Optional<ClientCredentialEntity> entityOpt = repository.findById(CLIENT_ID);

            if (entityOpt.isPresent()) {
                ClientCredentialEntity entity = entityOpt.get();

                // Clear all fields
                entity.setEncryptedClientId(null);
                entity.setEncryptedPassword(null);
                entity.setEncryptedToken(null);
                entity.setRegistered(false);
                entity.setActivated(false);

                repository.save(entity);
                log.info("All client credentials and status cleared successfully");
            } else {
                log.info("No existing credentials found to clear");
            }
        } catch (Exception e) {
            log.error("Failed to clear credentials: {}", e.getMessage(), e);
        }
    }

    public ClientStatus loadStatus() {
        Optional<ClientCredentialEntity> entityOpt = repository.findById(CLIENT_ID);

        if (entityOpt.isPresent()) {
            ClientCredentialEntity entity = entityOpt.get();
            String token = null;

            if (entity.getEncryptedToken() != null) {
                try {
                    token = encryptor.decrypt(entity.getEncryptedToken());
                } catch (Exception e) {
                    // Token might be corrupted, leave as null
                    log.warn("Failed to decrypt token: {}", e.getMessage());
                    // Clear corrupted token
                    entity.setEncryptedToken(null);
                    repository.save(entity);
                }
            }

            ClientStatus status = new ClientStatus(entity.isRegistered(), entity.isActivated(), token);
            log.info("Loaded client status from database: registered={}, activated={}, hasToken={}",
                    status.isRegistered(), status.isActivated(), (token != null));
            return status;
        }

        log.info("No client status found in database, returning default");
        return new ClientStatus(false, false, null);
    }

    private ClientCredentialEntity getOrCreateEntity() {
        return repository.findById(CLIENT_ID)
                .orElse(new ClientCredentialEntity());
    }

    // Same inner classes as before
    @Getter
    public static class ClientCredentials {
        private final String clientId;
        private final String password;

        public ClientCredentials(String clientId, String password) {
            this.clientId = clientId;
            this.password = password;
        }

    }

    @Getter
    public static class ClientStatus {
        private final boolean isRegistered;
        private final boolean isActivated;
        private final String token;

        public ClientStatus(boolean isRegistered, boolean isActivated, String token) {
            this.isRegistered = isRegistered;
            this.isActivated = isActivated;
            this.token = token;
        }

    }
}