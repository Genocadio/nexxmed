package com.nexxserve.medadmin.repository.admin;

import com.nexxserve.medadmin.entity.inventory.InventoryReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryReportRepository extends JpaRepository<InventoryReport, Long> {
}
