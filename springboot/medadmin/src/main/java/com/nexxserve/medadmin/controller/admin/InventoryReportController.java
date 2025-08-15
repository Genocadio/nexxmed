package com.nexxserve.medadmin.controller.admin;

import com.nexxserve.medadmin.dto.request.InventoryReportDTO;
import com.nexxserve.medadmin.entity.inventory.InventoryReport;
import com.nexxserve.medadmin.security.HasRoleClient;
import com.nexxserve.medadmin.service.admin.InventoryReportService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory-reports")
public class InventoryReportController {
    private final InventoryReportService inventoryReportService;

    public InventoryReportController(InventoryReportService inventoryReportService) {
        this.inventoryReportService = inventoryReportService;
    }

    @PostMapping
    @HasRoleClient
    public List<InventoryReport> saveReports(@RequestBody List<InventoryReportDTO> reports, HttpServletRequest httpServletRequest) {
        String remoteAddress = httpServletRequest.getRemoteAddr();
        String fullAddress = remoteAddress + ":5007";
        return inventoryReportService.saveReports(reports, fullAddress);
    }
}