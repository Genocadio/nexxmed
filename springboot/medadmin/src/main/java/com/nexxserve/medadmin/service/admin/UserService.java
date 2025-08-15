package com.nexxserve.medadmin.service.admin;

import com.nexxserve.medadmin.dto.request.UserReportDTO;
import com.nexxserve.medadmin.entity.clients.Client;
import com.nexxserve.medadmin.entity.clients.User;
import com.nexxserve.medadmin.repository.admin.UserRepository;
import com.nexxserve.medadmin.repository.clients.ClientRepository;
import com.nexxserve.medadmin.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    public List<User> saveUsers(List<UserReportDTO> users, String remoteAddress) {
        String clientId = SecurityUtils.getCurrentClientId();
        if (clientId == null) {
            throw new IllegalStateException("Client ID is not available in the security context");
        }
        Client client = clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new IllegalStateException("Client not found with ID: " + clientId));
        client.setBaseUrl(remoteAddress);
        clientRepository.save(client);

        List<User> userList = users.stream()
                .map(userDTO -> {
                    User user = userDTO.toEntity();
                    user.setClient(client);
                    return user;
                })
                .collect(Collectors.toList());

        return userRepository.saveAll(userList);
    }
}