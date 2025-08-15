package com.nexxserve.inventoryservice.event;

import com.nexxserve.inventoryservice.enums.ReportType;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class ReportFailureEvent extends ApplicationEvent {
    private final ReportType reportType;
    private final List<?> reports;
    private final String error;

    public ReportFailureEvent(Object source, ReportType reportType, List<?> reports, String error) {
        super(source);
        this.reportType = reportType;
        this.reports = reports;
        this.error = error;
    }
}