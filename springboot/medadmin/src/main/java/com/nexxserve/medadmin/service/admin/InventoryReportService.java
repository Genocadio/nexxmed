package com.nexxserve.medadmin.service.admin;

import com.nexxserve.medadmin.dto.request.InventoryReportDTO;
import com.nexxserve.medadmin.entity.clients.Client;
import com.nexxserve.medadmin.entity.inventory.InventoryReport;
import com.nexxserve.medadmin.repository.admin.InventoryReportRepository;
import com.nexxserve.medadmin.repository.clients.ClientRepository;
import com.nexxserve.medadmin.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryReportService {
    private final InventoryReportRepository inventoryReportRepository;
    private final ClientRepository clientRepository;

    public List<InventoryReport> saveReports(List<InventoryReportDTO> reports, String remoteAddress) {
        String clientId = SecurityUtils.getCurrentClientId();
        if (clientId == null) {
            throw new IllegalStateException("Client ID is not available in the security context");
        }
        Client client = clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new IllegalStateException("Client not found with ID: " + clientId));
        client.setBaseUrl(remoteAddress);
        clientRepository.save(client);

        List<InventoryReport> inventoryReports = reports.stream()
                .map(report -> {
                    InventoryReport inventoryReport = report.toEntity();
                    inventoryReport.setClient(client);
                    return inventoryReport;
                })
                .collect(Collectors.toList());

        return inventoryReportRepository.saveAll(inventoryReports);
    }
}