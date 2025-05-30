package com.nexxserve.billing.repository;

import com.nexxserve.billing.model.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
    List<InvoiceItem> findByInvoiceId(Long invoiceId);
    List<InvoiceItem> findByServiceId(Long serviceId);
    List<InvoiceItem> findByActivityId(Long activityId);
    List<InvoiceItem> findByConsumableId(Long consumableId);
    List<InvoiceItem> findByItemType(InvoiceItem.ItemType itemType);

    @Query("SELECT ii FROM InvoiceItem ii WHERE ii.invoice.patientId = :patientId")
    List<InvoiceItem> findByPatientId(@Param("patientId") String patientId);
}