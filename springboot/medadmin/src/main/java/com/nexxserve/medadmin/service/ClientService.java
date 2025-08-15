package com.nexxserve.medadmin.service;

import com.nexxserve.medadmin.dto.request.ActivateClientRequest;
import com.nexxserve.medadmin.dto.request.CreateClientRequest;
import com.nexxserve.medadmin.dto.response.ActivateClientResponse;
import com.nexxserve.medadmin.dto.response.CreateClientResponse;
import com.nexxserve.medadmin.dto.response.RefreshTokenResponse;
import com.nexxserve.medadmin.entity.Insurance;
import com.nexxserve.medadmin.entity.Owners;
import com.nexxserve.medadmin.entity.clients.Client;
import com.nexxserve.medadmin.enums.ClientStatus;
import com.nexxserve.medadmin.enums.SubscriptionType;
import com.nexxserve.medadmin.repository.InsuranceRepository;
import com.nexxserve.medadmin.repository.OwnersRepository;
import com.nexxserve.medadmin.repository.clients.ClientRepository;
import com.nexxserve.medadmin.service.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private OwnersRepository ownerRepository;

    @Autowired
    private InsuranceRepository insuranceRepository;

    @Autowired
    private JwtService jwtService;

    private final SecureRandom secureRandom = new SecureRandom();

    public CreateClientResponse createClient(CreateClientRequest createClientRequest) {
        Optional<Owners> ownerOpt = ownerRepository.findById(createClientRequest.getOwnerId());
        if (ownerOpt.isEmpty()) {
            throw new IllegalArgumentException("Owner not found with ID: " + createClientRequest.getOwnerId());
        }
        Owners owner = ownerOpt.get();
        List<Insurance> insurances = new ArrayList<>();
        if(createClientRequest.getInsuranceIds() != null && !createClientRequest.getInsuranceIds().isEmpty()) {
            insurances = insuranceRepository.findAllById(createClientRequest.getInsuranceIds());
        }


        List<Client> ownerClients = clientRepository.findAll()
                .stream()
                .filter(c -> c.getOwner().getId().equals(owner.getId()) && c.getName().equalsIgnoreCase(createClientRequest.getName()))
                .toList();
        if (!ownerClients.isEmpty()) {
            throw new IllegalArgumentException("Client with the same name already exists for this owner.");
        }


        String clientId = null;
        int attempts = 0;
        boolean exists;
        do {
            String generatedId = generateShortClientId(createClientRequest.getName());
            attempts++;
            exists = clientRepository.findAll().stream()
                    .anyMatch(c -> c.getOwner().getId().equals(owner.getId()) && c.getClientId().equals(generatedId));
            if (!exists) {
                clientId = generatedId;
                break;
            }
        } while (attempts < 5);

        if (clientId == null) {
            throw new IllegalStateException("Failed to generate unique clientId for owner.");
        }

        // Generate password
        String password = generatePassword();



        Client client = new Client();
        client.setClientId(clientId);
        client.setPassword(password);
        client.setInsurances(insurances);
        client.setName(createClientRequest.getName());
        client.setPhone(createClientRequest.getPhoneNumber());
        client.setBaseUrl(createClientRequest.getLocation());
        client.setOwner(owner);
        client.setStatus(ClientStatus.PENDING);
        client.setSubscriptionType(owner.getSubscriptionType());
        client.setActivationTime(null);

        if (createClientRequest.getPhoneNumber() != null) {
            client.setPhone(createClientRequest.getPhoneNumber());
        }
        client.setServiceType(createClientRequest.getServiceType());
        if (createClientRequest.getEmail() != null) {
            client.setEmail(createClientRequest.getEmail());
        }

        Client savedClient = clientRepository.save(client);


        return CreateClientResponse.fromEntity(savedClient);

    }

    public ActivateClientResponse activateClient(ActivateClientRequest request,  String remoteAddress) {
        Optional<Client> clientOpt = clientRepository.findByClientId(request.getClientId());
        if (clientOpt.isEmpty()) {
            throw new IllegalArgumentException("Client not found");
        }
        Client client = clientOpt.get();

        // Authenticate password
        if (!client.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        // Check email/phone against client and owner
        boolean match = false;
        if (request.getEmailOrPhone() != null) {
            String emailOrPhone = request.getEmailOrPhone().trim();
            if (emailOrPhone.equalsIgnoreCase(client.getEmail()) ||
                emailOrPhone.equalsIgnoreCase(client.getPhone()) ||
                (client.getOwner() != null && (
                    emailOrPhone.equalsIgnoreCase(client.getOwner().getEmail()) ||
                    emailOrPhone.equalsIgnoreCase(client.getOwner().getPhoneNumber())
                ))) {
                match = true;
            }
        }
        if (!match) {
            throw new IllegalArgumentException("Email or phone does not match client or owner");
        }

        // Activate client
        client.setBaseUrl(remoteAddress);
        client.setStatus(ClientStatus.ACTIVE);
        client.setLastTokenRefresh(LocalDateTime.now());
        client.setActivationTime(Instant.now());
        clientRepository.save(client);

        // Generate tokens
        String token = jwtService.generateToken(request.getClientId(), client.getName(), 4); // 4 days
        String refreshToken = jwtService.generateRefreshToken(request.getClientId(), client.getName(), 35); // 35 days

        return new ActivateClientResponse(token, refreshToken, "Client activated and tokens generated." );
    }

    public RefreshTokenResponse handleRefreshToken(String clientId, String remoteAddress) {
        Optional<Client> clientOpt = clientRepository.findByClientId(clientId);
        if (clientOpt.isEmpty()) {
            return new RefreshTokenResponse(null, null, "Client not found");
        }
        Client client = clientOpt.get();

        if (client.getStatus() == ClientStatus.DEACTIVATED) {
            return new RefreshTokenResponse(null, null, "Client is deactivated");
        }

        // Check subscription expiry
        Date expiry = calculateTokenExpiry(client);
        if (expiry.before(new Date())) {
            client.setStatus(ClientStatus.DEACTIVATED);
            clientRepository.save(client);
            return new RefreshTokenResponse(null, null, "Subscription expired");
        }



        // Generate new tokens with expiry
        long millis = expiry.getTime() - System.currentTimeMillis();
        int days = (int) Math.ceil(millis / (1000.0 * 60 * 60 * 24));
        client.setLastTokenRefresh(LocalDateTime.now());
        client.setBaseUrl(remoteAddress);
        clientRepository.save(client);
        String newAccessToken = jwtService.generateToken(clientId, client.getName(), days);
        String newRefreshToken = jwtService.generateRefreshToken(clientId, client.getName(), days);

        return new RefreshTokenResponse(newAccessToken, newRefreshToken, "Token refreshed");
    }



    private Date calculateTokenExpiry(Client client) {
        Instant activation = client.getActivationTime();
        SubscriptionType type = client.getSubscriptionType();
        ZonedDateTime activationZdt = activation.atZone(ZoneId.systemDefault());
        ZonedDateTime expiryZdt;

        if (type == SubscriptionType.MONTHLY) {
            expiryZdt = activationZdt.plusMonths(1)
                .with(TemporalAdjusters.lastDayOfMonth())
                .plusDays(5);
        } else {
            expiryZdt = activationZdt.plusYears(1)
                .with(TemporalAdjusters.lastDayOfYear())
                .plusDays(5);
        }
        return Date.from(expiryZdt.toInstant());
    }

    private String generateShortClientId(String name) {
        String[] words = name.trim().split("\\s+");
        String prefix;
        if (words.length >= 2) {
            prefix = ("" + words[0].charAt(0) + words[1].charAt(0)).toUpperCase();
        } else {
            prefix = name.substring(0, Math.min(2, name.length())).toUpperCase();
        }
        int randomNum = 100 + secureRandom.nextInt(900); // random 3-digit number
        return prefix + randomNum;
    }


    public Optional<Client> authenticateClient(String clientId, String password) {
        Optional<Client> client = clientRepository.findByClientId(clientId);
        if (client.isPresent() && client.get().getPassword().equals(password)) {
            return client;
        }
        return Optional.empty();
    }

    public String generateTokenForClient(String clientId, String password) {
        Optional<Client> clientOpt = authenticateClient(clientId, password);
        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            if (client.getStatus() == ClientStatus.ACTIVE) {
                client.setLastTokenRefresh(LocalDateTime.now());
                clientRepository.save(client);
                return jwtService.generateToken(clientId, client.getName(), 4);
            }
        }
        return null;
    }


    public Client activateClient(String clientId) {
        Optional<Client> client = clientRepository.findByClientId(clientId);
        if (client.isPresent()) {
            Client c = client.get();
            c.setStatus(ClientStatus.ACTIVE);
            return clientRepository.save(c);
        }
        return null;
    }

    public Client deactivateClient(String clientId) {
        Optional<Client> client = clientRepository.findByClientId(clientId);
        if (client.isPresent()) {
            Client c = client.get();
            c.setStatus(ClientStatus.DEACTIVATED);
            return clientRepository.save(c);
        }
        return null;
    }


    public List<CreateClientResponse> getAllClients() {

        return clientRepository.findAll().stream().map(
                CreateClientResponse::fromEntity
        ).toList();
    }



    public List<Client> getExpiredClients() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        return clientRepository.findActiveClientsWithExpiredTokens(threshold);
    }


    private String generatePassword() {
        byte[] bytes = new byte[16];
        secureRandom.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}