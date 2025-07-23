package com.nexxserve.medadmin.service;

import com.nexxserve.medadmin.dto.Insurance.InsuranceResponseDto;
import com.nexxserve.medadmin.dto.request.CreateClientRequest;
import com.nexxserve.medadmin.dto.response.CreateClientResponse;
import com.nexxserve.medadmin.entity.Insurance;
import com.nexxserve.medadmin.entity.Owners;
import com.nexxserve.medadmin.entity.clients.Client;
import com.nexxserve.medadmin.enums.ClientStatus;
import com.nexxserve.medadmin.repository.InsuranceRepository;
import com.nexxserve.medadmin.repository.OwnersRepository;
import com.nexxserve.medadmin.repository.clients.ClientRepository;
import com.nexxserve.medadmin.service.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;
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

        Client savedClient = clientRepository.save(client);

        // Build response
        CreateClientResponse response = new CreateClientResponse();
        response.setClientId(savedClient.getClientId());
        response.setPassword(savedClient.getPassword());
        response.setName(savedClient.getName());
        response.setPhoneNumber(savedClient.getPhone());
        response.setEmail(createClientRequest.getEmail());
        response.setLocation(savedClient.getBaseUrl());
        response.setOwner(owner);
        response.setInsurances(savedClient.getInsurances().stream()
                .map(InsuranceResponseDto::fromEntity)
                .toList());

        return CreateClientResponse.fromEntity(savedClient);

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

//    public Client registerClient(String name, String phone, String baseUrl) {
//        // Check if a client with the same name and phone already exists
//        Optional<Client> existingClient = clientRepository.findByNameAndPhone(name, phone);
//
//        if (existingClient.isPresent()) {
//            // Return the existing client with its credentials
//            return existingClient.get();
//        }
//
//        // If no client exists, create a new one
//        String clientId = generateClientId();
//        String password = generatePassword();
//
//        Client client = new Client(clientId, password, name, phone, baseUrl);
//        return clientRepository.save(client);
//    }

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
                return jwtService.generateToken(clientId, client.getName());
            }
        }
        return null;
    }

    public String refreshToken(String currentToken) {
        if (jwtService.isTokenValid(currentToken) && !jwtService.isTokenExpired(currentToken)) {
            String clientId = jwtService.getClientIdFromToken(currentToken);
            Optional<Client> clientOpt = clientRepository.findByClientId(clientId);

            if (clientOpt.isPresent()) {
                Client client = clientOpt.get();
                if (client.getStatus() == ClientStatus.ACTIVE) {
                    // Check if token refresh is within 30 days
                    LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
                    if (client.getLastTokenRefresh() == null || client.getLastTokenRefresh().isAfter(thirtyDaysAgo)) {
                        client.setLastTokenRefresh(LocalDateTime.now());
                        clientRepository.save(client);
                        return jwtService.generateToken(clientId, client.getName());
                    }
                }
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

    public ClientStatus getClientStatus(String clientId) {
        Optional<Client> client = clientRepository.findByClientId(clientId);
        return client.map(Client::getStatus).orElse(null);
    }

    public List<CreateClientResponse> getAllClients() {

        return clientRepository.findAll().stream().map(
                CreateClientResponse::fromEntity
        ).toList();
    }

    public void updateHealthCheck(String clientId) {
        Optional<Client> client = clientRepository.findByClientId(clientId);
        if (client.isPresent()) {
            Client c = client.get();
            c.setLastHealthCheck(LocalDateTime.now());
            clientRepository.save(c);
        }
    }

    public List<Client> getExpiredClients() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        return clientRepository.findActiveClientsWithExpiredTokens(threshold);
    }

    private String generateClientId() {
        return "client_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    private String generatePassword() {
        byte[] bytes = new byte[16];
        secureRandom.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}